How to submit a bug report
---

Before creating an issue, make sure:
  1. Your title and content is not confusing or content-less.
  2. All texts are written in proper English.
  
If it's a bug or problem:
  1. This bug can be reproduced.
  2. This bug can be found in latest build.
  3. Dumps, backtraces or files are provided.
  4. It's you yourself who first found this bug.
  
If it's an advice or a feature request:
  1. This feature does not exist in latest build.
  2. This feature is logical and clear-cut.
  3. It's you yourself who first came up with the idea.
 
Nukkit will create a bug report for EVERY exception and error detected, and there are some columns you need to fill out in the report. If multiple exceptions are triggered, you should combine the stacktrace into one report and then submit the report.

In the report, you can see if the error is caused by Nukkit or a plugin. However, when "PLUGIN ERROR" is "false" and there are plugins running, it does not necessarily indicates that the error is caused by Nukkit.
 
To sumbit bugs and problems, please upload the automaticly generated report. Make sure you have filled in all blanks in the template. Please provide **as much information as you could**, or our developers might got stuck or confused when looking into your issue. 

To submit feature requests and suggestions, please explicitly describe the feature you want or your suggestion.

Note that the Issues section on GitHub is not for contents that are not related to the two categories listed above. Irrelevant issues will be closed. Please visit our forums for other kinds of discussions.

Example
---

### Issue Description
<!--- Use our forum https://forums.nukkit.io for questions -->
It seems that the player you are manipulating does not seem to be moving from other people, and it seems that you are not moving from others.

I do not know because I have not logged in to anything other than my server, but it works normally with Wi-Fi multi.

### OS and Versions
<!--- Use the 'version' command in Nukkit -->
* Nukkit Version: https://github.com/Nukkit/Nukkit/pull/1517  <!--- Do not just write "latest" or "1.0dev" here. Write compile time is also fine. -->
<!--- Use 'java -version' in command line -->
* Java Version: 
```
java version "9"
Java(TM) SE Runtime Environment (build 9+175)
Java HotSpot(TM) 64-Bit Server VM (build 9+175, mixed mode)
```
<!--- Device and host configuration, such as: 8GB RAM, 12-core Intel X5650 CPU, 100Mb internet upload. You may get this info from your host provider or hardware information softwares -->
* Host Configuration: 
<!-- Do NOT write "doesn't matters", it DOES matters. I met a guy shouting his world can not be saved, after we looked into storage, we found that his SATA wire is not connected. -->

| Item | Value |
|:----:|:-----:|
| Host OS | Microsoft Windows [10.0.10240] |  <!-- What OS do you use to open Nukkit in? Linux? Windows? Write it here -->
| Memory(RAM) | 4 GB | <!-- Open your task manager in windows, or use command "top" in linux -->
| Storage Size | 1 TB | <!-- Max size -->
| Storage Type | SSD | <!-- SSD or HDD -->
| CPU Type | Intel Xeon X5650 | <!-- Such as: "Intel Xeon X5650" ,"Hisilicon HI3536C" or "AMD Ryzen 7" -->
| CPU Core Count | 12 cores 24 threads | 
| Upstream Bandwidth | 100 Mbps | <!-- How many Mbps/Gbps? Such as: 100 Mbps or 1 Gbps. If you are testing in LAN (wired or wifi) , it depends on speed of your router, it is normally 100 Mbps. -->

* Client Configuration: 

| Item | Value |
|:----:|:-----:|
| Client Edition | Android | <!--- Windows 10? Android? iOS? Simulators with x86 platform? -->
| Client Version | 1.0.4 | <!--- Client Version, such as 1.1.2, 0.15.90 or 0.15 build 1 and so on -->

```
### Issue Description
<!--- Use our forum https://forums.nukkit.io for questions -->
It seems that the player you are manipulating does not seem to be moving from other people, and it seems that you are not moving from others.

I do not know because I have not logged in to anything other than my server, but it works normally with Wi-Fi multi.

### OS and Versions
<!--- Use the 'version' command in Nukkit -->
* Nukkit Version: https://github.com/Nukkit/Nukkit/pull/1517  <!--- Do not just write "latest" or "1.0dev" here. Write compile time is also fine. -->
<!--- Use 'java -version' in command line -->
* Java Version: 

java version "9"
Java(TM) SE Runtime Environment (build 9+175)
Java HotSpot(TM) 64-Bit Server VM (build 9+175, mixed mode)

<!--- Device and host configuration, such as: 8GB RAM, 12-core Intel X5650 CPU, 100Mb internet upload. You may get this info from your host provider or hardware information softwares -->
* Host Configuration: 
<!-- Do NOT write "doesn't matters", it DOES matters. I met a guy shouting his world can not be saved, after we looked into storage, we found that his SATA wire is not connected. -->

| Item | Value |
|:----:|:-----:|
| Host OS | Microsoft Windows [10.0.10240] |  <!-- What OS do you use to open Nukkit in? Linux? Windows? Write it here -->
| Memory(RAM) | 4 GB | <!-- Open your task manager in windows, or use command "top" in linux -->
| Storage Size | 1 TB | <!-- Max size -->
| Storage Type | SSD | <!-- SSD or HDD -->
| CPU Type | Intel Xeon X5650 | <!-- Such as: "Intel Xeon X5650" ,"Hisilicon HI3536C" or "AMD Ryzen 7" -->
| CPU Core Count | 12 cores 24 threads | 
| Upstream Bandwidth | 100 Mbps | <!-- How many Mbps/Gbps? Such as: 100 Mbps or 1 Gbps. If you are testing in LAN (wired or wifi) , it depends on speed of your router, it is normally 100 Mbps. -->

* Client Configuration: 

| Item | Value |
|:----:|:-----:|
| Client Edition | Android | <!--- Windows 10? Android? iOS? Simulators with x86 platform? -->
| Client Version | 1.0.4 | <!--- Client Version, such as 1.1.2, 0.15.90 or 0.15 build 1 and so on -->

```
