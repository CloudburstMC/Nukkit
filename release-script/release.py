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

from xml.dom import minidom

from release_utils import *

pom_doc = minidom.parse('pom.xml')
project_name = None
project_version = None
is_windows = os.name == 'nt'

version_tags = pom_doc.getElementsByTagName('version')
project_version_tag = None
for version_tag in version_tags:
    if version_tag.parentNode is not None and version_tag.parentNode.nodeName == 'project':
        project_version_tag = version_tag
        project_version = get_text(version_tag.childNodes)
        break

name_tags = pom_doc.getElementsByTagName('name')
for name_tag in name_tags:
    if name_tag.parentNode is not None and name_tag.parentNode.nodeName == 'project':
        project_name = get_text(name_tag.childNodes)
        break

check(project_version is not None, "Could not parse the project version!")
check(project_name is not None, "Could not parse the project name!")
check(len(project_version) > 0, "The project version cannot be empty!")
check(len(project_name) > 0, "The project name cannot be empty!")
check(project_version.upper() == project_version, "The project version must be all uppercase!")
check("SNAPSHOT" not in project_version, "Attempted to release a snapshot version! Version: " + project_version)
check("CUSTOM" not in project_version, "The release version has CUSTOM identifier! Make sure to remove it if the "
                                       "project state is really ready for publishing! Version: " + project_version)
check('-PN' in project_version, "The project version is missing the '-PN' suffix! Version: " + project_version)

check_support('gpg --version')
if create_git_commit or create_git_tag or create_git_branch:
    check_support('git --version')
if run_docker_build or run_docker_push:
    check_support('docker --version')
if not use_mvn_wrapper:
    check_support('mvn -version')

log('-> GPG keys:', cmd('gpg', '--list-keys'))

start_progress("Build setup")
log("-> Preparing to publish", project_name, project_version)

git_is_dirty = cmd('git', 'status', '--porcelain')
if not ignore_dirty_state:
    check(not git_is_dirty, "The workspace is dirty!\n" + git_is_dirty)


set_build_number(project_version)
git_tag = 'v' + project_version
try:
    existing_tag = cmd('git', 'rev-parse', git_tag)
    raise failure("The tag " + git_tag + " is already referencing the commit " + existing_tag)
except subprocess.CalledProcessError as err:
    log("-> The tag", git_tag, "is available")

mvn = 'mvn'
ntp = '--no-transfer-progress'
if use_mvn_wrapper:
    if is_windows:
        mvn = 'mvnw.cmd'
    else:
        mvn = './mvnw'

log("-> Checking java version")
adjust_java_home(mvn, project_name, check_java_version)
finish_progress()

if run_test_build:
    start_progress("Test build")
    log('-> Executing a test build with', mvn, 'clean package')
    args = [mvn, ntp, 'clean', 'package', '-Dmaven.javadoc.skip=true']
    if run_tests:
        args += ['-DskipTests=false']
    else:
        args += ['-DskipTests=true']
    status_code = subprocess.call(args)
    check(status_code == 0, "Could not execute a normal build! Maven returned status code " + str(status_code))
    finish_progress()

start_progress("Adjusting GIT repository")
git_branch = None
if create_git_branch:
    git_branch = 'release/' + git_tag
    if git_branch != cmd('git', 'branch', '--show-current'):
        log("-> Creating branch", git_branch)
        cmd('git', 'checkout', '-b', git_branch)

if create_git_branch or create_git_commit:
    if git_branch is None:
        git_branch = cmd('git', 'branch', '--show-current')
    log('-> Current branch:', git_branch)

if create_git_commit:
    log("-> Executing the build commit")
    commit_cmd('git', 'commit', '--allow-empty', '-m', 'Releasing ' + project_name + ' ' + git_tag)

if create_git_tag:
    log("-> Creating tag", git_tag)
    cmd('git', 'tag', git_tag)
finish_progress()

docker_tags = []
try:
    if run_docker_build or run_docker_build_pterodactyl:
        def build_docker(tag, source):
            global docker_tags
            log('-> Executing a docker build for tag', tag)
            stt = subprocess.call(('docker', 'build', '-t', tag, source))
            check(stt == 0, "Could not execute a docker build! Status code: " + str(stt))
            docker_tags += [tag]

        docker_version = project_version.replace('-PN', '')
        docker_tag = docker_tag_prefix + ':' + docker_version
        docker_version_parts = docker_version.split('.')
        is_snapshot = docker_version_parts[-1].lower() == "snapshot"
        if is_snapshot:
            docker_version_parts.pop()
        elif run_docker_build:
            start_progress("Executing Docker build")
            build_docker(docker_tag, '.')
            finish_progress()

        def pterodactyl_tag_name(base, java):
            return base + '-pterodactyl-java-' + java

        def build_pterodactyl(java):
            base = docker_tag
            if is_snapshot:
                base = "bleeding"
            build_docker(pterodactyl_tag_name(base, java), './pterodactyl-image-java'+java+'.Dockerfile')

        if run_docker_build_pterodactyl:
            start_progress("Building pterodactyl images")
            build_pterodactyl(8)
            build_pterodactyl(11)
            finish_progress()

        if not is_snapshot:
            start_progress("Tagging Docker image")
            for i in range(len(docker_version_parts)):
                docker_version_parts.pop()
                if len(docker_version_parts) > 0 and docker_version_parts[-1].isnumeric():
                    docker_sub_version = ".".join(docker_version_parts)
                elif len(docker_version_parts) == 0:
                    docker_sub_version = "latest"
                else:
                    break

                if docker_sub_version is not None:
                    def add_tag(from_tag, to_tag):
                        global docker_tags
                        log("-> Adding docker tag", to_tag)
                        cmd('docker', 'tag', from_tag, to_tag)
                        docker_tags += [to_tag]

                    subversion_tag = docker_tag_prefix + ':' + docker_sub_version
                    add_tag(docker_tag, subversion_tag)
                    if run_docker_build_pterodactyl:
                        add_tag(pterodactyl_tag_name(docker_tag, 8), pterodactyl_tag_name(subversion_tag, 8))
                        add_tag(pterodactyl_tag_name(docker_tag, 11), pterodactyl_tag_name(subversion_tag, 11))
            finish_progress()

    if run_maven_deploy:
        start_progress("Executing Maven Deploy")
        log('-> Executing a maven deploy with', mvn, 'clean deploy')
        args = [mvn, ntp, 'clean', 'deploy']
        if run_test_build or not run_tests:
            args += ['-DDskipTests=true']
        else:
            args += ['-DDskipTests=false']
        status_code = subprocess.call(args)
        check(status_code == 0, "Could not execute the maven deploy! Maven returned status code " + str(status_code))
        finish_progress()
except Exception as e:
    log("-> The release has failed! Removing release commit and the git tag", git_tag, file=sys.stderr)
    cmd('git', 'tag', '-d', git_tag)
    cmd('git', 'reset', '--soft', 'HEAD~1')
    raise e

if run_docker_push:
    start_progress("Pushing Docker image and tags")
    for docker_tag_to_push in docker_tags:
        log('-> Pushing docker tag', docker_tag_to_push)
        cmd('docker', 'push', docker_tag_to_push)
    finish_progress()

if update_pom_version:
    start_progress("Updating pom.xml")
    next_version = project_version + '-CUSTOM'
    log('-> Changing the project version to', next_version)
    for node in project_version_tag.childNodes:
        project_version_tag.removeChild(node)
    project_version_tag.appendChild(pom_doc.createTextNode(next_version))

    pom_output = open("pom.xml", "w")
    try:
        pom_doc.documentElement.writexml(pom_output)
    finally:
        pom_output.close()

    if create_git_commit:
        cmd('git', 'add', 'pom.xml')
        commit_cmd('git', 'commit', '-m', 'Version changed to ' + next_version)
    finish_progress()

if run_git_push:
    start_progress("Pushing GIT tag and commits")
    if set_github_remote:
        log("-> Setting the git remote", git_remote_name, "to", git_remote_url)
        try:
            log("Trying to add...")
            cmd('git', 'remote', 'add', git_remote_name, git_remote_url)
        except subprocess.CalledProcessError:
            log("Trying to set...")
            cmd('git', 'remote', 'set-url', git_remote_name, git_remote_url)
        log("-> Fetching from ", git_remote_name)
        cmd('git', 'fetch', git_remote_name)
    else:
        git_remote_name = 'origin'

    log("Remote:", git_remote_name)
    log("Branch:", git_branch)

    if create_git_commit:
        log("-> Pushing commits to the Git repository")
        cmd('git', 'push', '-u', git_remote_name, git_branch)
    if create_git_tag:
        print("-> Pushing tag", git_tag, "to the Git repository")
        cmd('git', 'push', git_remote_name, git_tag)
    finish_progress()

log("-> The build script has completed")
