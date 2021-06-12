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

from release_config import *

# Here you can override any thing from the release_config.py that you want
# without the risk of it going into the git repository

# ignore_dirty_state = True
# use_mvn_wrapper = False
# check_java_version = False
# run_test_build = False
# run_tests = False
# run_docker_build = False
# run_docker_push = False
# run_docker_build_pterodactyl = True
# run_maven_deploy = False
# run_git_push = False
# create_git_tag = False
create_git_branch = False
# create_git_commit = False
update_pom_version = False
# docker_tag_prefix = 'powernukkit/powernukkit'
git_config_bot['commit.gpgsign'] = 'false'
