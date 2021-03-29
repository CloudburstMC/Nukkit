#  https://PowerNukkit.org - The Nukkit you know but Powerful!
#  Copyright (C) 2021  José Roberto de Araújo Júnior
#
#  This program is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program.  If not, see <http://www.gnu.org/licenses/>.

import os
import subprocess
import sys

from release_config import *

current_dir = os.getcwd()
module_dir = current_dir
if os.getcwd().endswith(os.sep + 'release-script'):
    current_dir = current_dir[0:-14]
    print('-> Changing the working directory to', current_dir)
    os.chdir(current_dir)

if os.path.isfile('release-script' + os.sep + 'release_config_local.py'):
    from release_config_local import *

print('-> The working directory is', current_dir)


def get_text(nodelist):
    rc = []
    for node in nodelist:
        if node.nodeType == node.TEXT_NODE:
            rc.append(node.data)
    return ''.join(rc)


def failure(reason):
    print("FAILED:", reason, file=sys.stderr)
    return Exception(reason)


def check(condition, message):
    if message is None:
        raise failure("Bad usage of the check function, the message argument was not provided")
    if not condition:
        raise failure(message)


def cmd(*command):
    return subprocess.check_output(
        command, stderr=subprocess.DEVNULL
    ).decode().strip()


def set_git_config(config, value):
    check(len(config) > 0, "The git config key must not be empty")
    if value is None:
        cmd('git', 'config', '--unset', config)
    else:
        cmd('git', 'config', config, value)


git_config_backups = {}
for _key in git_config_bot:
    try:
        git_config_backups[_key] = cmd('git', 'config', _key)
    except subprocess.CalledProcessError:
        git_config_backups[_key] = None


def commit_cmd(*command):
    try:
        for key in git_config_backups:
            set_git_config(key, git_config_bot[key])
        return cmd(*command)
    finally:
        for key in git_config_backups:
            set_git_config(key, git_config_backups[key])
