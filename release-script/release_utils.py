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
    return subprocess.check_output(command).decode().strip()


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


def adjust_java_home(mvn, project_name, raise_failures, looped=False):
    version_lines = ['Java version: Unknown']
    try:
        version_lines = subprocess.check_output([mvn, '-version']).decode().strip().split("\n")
    except:
        log('There are issues with JAVA_HOME! Trying to find JDK_1_8!')
        
    for version_line in version_lines:
        if "Java version: " in version_line:
            print("->", version_line)
            if "Java version: 1.8" not in version_line:
                jdk_8_home = os.getenv('JDK_1_8')
                java_home = os.getenv('JAVA_HOME')
                if jdk_8_home and java_home != jdk_8_home:
                    print("-> Found an alternative JDK 1.8 at", jdk_8_home)
                    os.putenv('JAVA_HOME', jdk_8_home)
                    if not looped:
                        adjust_java_home(mvn, project_name, raise_failures, True)
                        break
                if raise_failures:
                    raise failure(project_name + " must be built with Java 8 (1.8)")


progress = []


def is_teamcity():
    if os.getenv('TEAMCITY_VERSION'):
        # log("TeamCity: true")
        return True
    # log("TeamCity: false")
    return False


def start_progress(*message):
    if is_teamcity():
        global progress
        msg = " ".join(message)
        progress += [msg]
        print("##teamcity[blockOpened name='" + msg + "']", flush=True)
    print("-->", *message)


def finish_progress():
    if is_teamcity():
        print("##teamcity[blockClosed name='" + progress.pop() + "']", flush=True)


def log(*args):
    print(*args, flush=True)


def set_build_number(version):
    if is_teamcity():
        print("##teamcity[buildNumber '" + version + "']", flush=True)


def check_support(args):
    try:
        log('OK:', args, cmd(*args.split(' ')).split("\n")[0])
    except Exception as e:
        log('FAIL:', args)
        raise e
