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

ignore_dirty_state = False
use_mvn_wrapper = True
check_java_version = True
run_test_build = True
run_tests = True
run_docker_build = True
run_docker_push = True
run_maven_deploy = True
run_git_push = True
create_git_tag = True
create_git_branch = True
create_git_commit = True
update_pom_version = True
docker_tag_prefix = 'powernukkit/powernukkit'
git_config_bot = {
    'user.signingkey': 'BCB3AD96313CC7D292E58CA611DF304775A727C6',
    'commit.gpgsign': 'true',
    'user.name': 'PowerNukkit',
    'user.email': 'github-bot@powernukkit.org'
}
