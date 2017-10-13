# 将Spring Boot的Java应用部署成为Windows的服务

## TAG
Spring Boot, Windows Service

本文主要说明使用Spring Boot开发的Java应用，如何将其注册成Windows的服务。

## 前提

### .NET版本要求
Windows系统需要安装.NET 4.0以上版本，安装过程此文略去。

### WinSW
WinSW是一个开源项目，项目地址在：[https://github.com/kohsuke/winsw]

#### 安装WinSW
如果不想自己编译WinSW，可以在此处下载二进制文件：[http://repo.jenkins-ci.org/releases/com/sun/winsw/winsw/]

选择一个版本，推荐2.1.2版本。然后下载winsw-2.1.2-bin.exe文件。

下载完成后将文件复制到一个新目录下，如C:\winsw。重命名为winsw.exe，安装完成。

### Spring Boot应用打包
使用maven将你的Spring Boot应用build成jar ball。

如ifpserver项目，用mvn clean package命令打包后，可在项目的target目录下面生成一个jar文件，重命名该jar文件为ifpserver.jar。

## 配置WinSW XML文件
在C:\winsw目录下创建一个winsw.xml文件，注意此处的xml文件名称必须与WinSW的可执行文件同名。

编辑该xml文件：

	<service>
		<!-- 此处定义注册后的Windows服务的ID -->
		<id>ifpserver</id>

		<!-- 此处定义注册后的Windows服务的名称 -->
		<name>ifpserver</name>

		<!-- 此处定义注册后的Windows服务的描述内容，可在Windows服务管理器中看到 -->
		<description>FAP Server Test</description>

		<!-- env定义的是该服务运行时的环境变量 -->
		<!-- 此处定义的是Java的安装目录 -->
		<env name="JAVA_HOME" value="C:\Program Files\Java\jdk1.8.0_111" />
		<!-- 此处定义的是应用的目录 -->
		<env name="APP_HOME" value="D:\fast-workspace\ifpserver" />

		<!-- 此处定义的是服务的可执行文件，Spring Boot应用都是java -->
		<executable>%JAVA_HOME%\bin\java.exe</executable>

		<!-- 此处定义的是执行该Spring Boot应用的参数，最重要的是定义-jar中的jar文件，其他JVM参数可问度娘 -->
		<arguments>-Xrs -Xms512m -Xmx2048m -jar "%APP_HOME%\target\ifpserver.jar"</arguments>

		<!-- Windows服务的日志模式 -->
		<logmode>rotate</logmode>
	</service>

## 注册服务、启动服务
打开一个命令行工具，如PowerShell（最好使用管理员模式运行该命令行工具）。

然后cd C:\winsw。切换到WinSW的安装目录。

执行
	.\winsw install
即可将配置中的那个Spring Boot应用加入到Windows服务当中。

执行
	.\winsw start
即可启动配置的那个Spring Boot应用服务。

## 停止服务、删除服务
打开一个命令行工具，如PowerShell（最好使用管理员模式运行该命令行工具）。

然后cd C:\winsw。切换到WinSW的安装目录。

执行
	.\winsw stop
即可停止配置的那个Spring Boot应用服务。

执行
	.\winsw uninstall
即可将配置的那个Spring Boot应用从Windows服务中删除。

## 其他
事实上，此文是以Spring Boot应用为例来说明的，但是WinSW实际上可以作为任何应用程序注册成Windows服务的工具。

Enjoy.