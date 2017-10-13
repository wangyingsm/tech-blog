# ��Spring Boot��JavaӦ�ò����ΪWindows�ķ���

## TAG
Spring Boot, Windows Service

������Ҫ˵��ʹ��Spring Boot������JavaӦ�ã���ν���ע���Windows�ķ���

## ǰ��

### .NET�汾Ҫ��
Windowsϵͳ��Ҫ��װ.NET 4.0���ϰ汾����װ���̴�����ȥ��

### WinSW
WinSW��һ����Դ��Ŀ����Ŀ��ַ�ڣ�[https://github.com/kohsuke/winsw]

#### ��װWinSW
��������Լ�����WinSW�������ڴ˴����ض������ļ���[http://repo.jenkins-ci.org/releases/com/sun/winsw/winsw/]

ѡ��һ���汾���Ƽ�2.1.2�汾��Ȼ������winsw-2.1.2-bin.exe�ļ���

������ɺ��ļ����Ƶ�һ����Ŀ¼�£���C:\winsw��������Ϊwinsw.exe����װ��ɡ�

### Spring BootӦ�ô��
ʹ��maven�����Spring BootӦ��build��jar ball��

��ifpserver��Ŀ����mvn clean package�������󣬿�����Ŀ��targetĿ¼��������һ��jar�ļ�����������jar�ļ�Ϊifpserver.jar��

## ����WinSW XML�ļ�
��C:\winswĿ¼�´���һ��winsw.xml�ļ���ע��˴���xml�ļ����Ʊ�����WinSW�Ŀ�ִ���ļ�ͬ����

�༭��xml�ļ���

	<service>
		<!-- �˴�����ע����Windows�����ID -->
		<id>ifpserver</id>

		<!-- �˴�����ע����Windows��������� -->
		<name>ifpserver</name>

		<!-- �˴�����ע����Windows������������ݣ�����Windows����������п��� -->
		<description>FAP Server Test</description>

		<!-- env������Ǹ÷�������ʱ�Ļ������� -->
		<!-- �˴��������Java�İ�װĿ¼ -->
		<env name="JAVA_HOME" value="C:\Program Files\Java\jdk1.8.0_111" />
		<!-- �˴��������Ӧ�õ�Ŀ¼ -->
		<env name="APP_HOME" value="D:\fast-workspace\ifpserver" />

		<!-- �˴�������Ƿ���Ŀ�ִ���ļ���Spring BootӦ�ö���java -->
		<executable>%JAVA_HOME%\bin\java.exe</executable>

		<!-- �˴��������ִ�и�Spring BootӦ�õĲ���������Ҫ���Ƕ���-jar�е�jar�ļ�������JVM�������ʶ��� -->
		<arguments>-Xrs -Xms512m -Xmx2048m -jar "%APP_HOME%\target\ifpserver.jar"</arguments>

		<!-- Windows�������־ģʽ -->
		<logmode>rotate</logmode>
	</service>

## ע�������������
��һ�������й��ߣ���PowerShell�����ʹ�ù���Աģʽ���и������й��ߣ���

Ȼ��cd C:\winsw���л���WinSW�İ�װĿ¼��

ִ��
	.\winsw install
���ɽ������е��Ǹ�Spring BootӦ�ü��뵽Windows�����С�

ִ��
	.\winsw start
�����������õ��Ǹ�Spring BootӦ�÷���

## ֹͣ����ɾ������
��һ�������й��ߣ���PowerShell�����ʹ�ù���Աģʽ���и������й��ߣ���

Ȼ��cd C:\winsw���л���WinSW�İ�װĿ¼��

ִ��
	.\winsw stop
����ֹͣ���õ��Ǹ�Spring BootӦ�÷���

ִ��
	.\winsw uninstall
���ɽ����õ��Ǹ�Spring BootӦ�ô�Windows������ɾ����

## ����
��ʵ�ϣ���������Spring BootӦ��Ϊ����˵���ģ�����WinSWʵ���Ͽ�����Ϊ�κ�Ӧ�ó���ע���Windows����Ĺ��ߡ�

Enjoy.