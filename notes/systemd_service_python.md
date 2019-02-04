# 使用Python写一个systemd服务

本教程翻译至原作者[torfsen](https://github.com/torfsen)的[github repo python_systemd_tutorial](https://github.com/torfsen/python-systemd-tutorial)，本文遵循与原教程相同的协议。 

大部分的Linux发行版都使用[systemd]来管理系统的服务（或者叫*守护精灵*），例如在系统启动的时候自动按照正确的顺序启动某些服务。

使用Python写一个systemd的服务其实比较简单，但是systemd本身的复杂性在一开始可能会让你觉得艰巨。这份教程的目的就是让你入门。

当你找不到解决方案或者需要详细资料时，请到[systemd documentation][systemd]寻找答案，这里的内容非常扩展和全面。然而，这里的文档分散在许多页面当中，寻找你想要的内容并非总是容易的。[systemd.directives]是一个很好的地方用来查找systemd中某个特定的详细资料，这里列表展示了所有的配置选项，命令行参数，等等，并提供了响应文档的链接。

除了这个`REAME.md`文件之外，这个仓库还包括了一个Python的服务例子(`python_demo_service.py`)，以及一个systemd的unit文件例子。

我们使用的systemd的版本是229，如果你不是使用这个版本(可使用`systemctl --version`来检查你的版本)，然后查询systemd文档以确认哪些部分可能会有区别。

## 系统和用户服务

systemd既支持*系统*又支持*用户*服务。系统服务是指服务是运行在系统自身的systemd实例上并且为整个系统和所有用户提供所有功能。用户服务，运行在独立的systemd实例之上并且和某个特定的用户绑定。

如果你的目标是创建一个系统服务，最好的方案还是从创建一个用户服务开始，因为这样可以让你集中精力在将一个服务运行起来，同时不用特别关注设置一个系统服务的复杂性之上。这篇教程大部分内容集中在用户服务之上，但[最后有个章节](#creating-a-system-service)会讲述如何从用户服务走向系统服务。

## 创建一个用户服务

### 服务单元文件

要创建一个systemd服务，你需要创建一个相应的*服务单元文件*，该文件是一个纯文本的、ini结构的配置文件。这篇教程中我们会使用一个简单的自包含的单元文件，如果你需要高级进阶内容，请参考[systemd.unit]。

用户服务单元文件可以放置在[多个位置](https://www.freedesktop.org/software/systemd/man/systemd.unit.html#User%20Unit%20Search%20Path)。其中的某些位置需要root权限，但其中也有一些位置处于你的用户根目录中。对于这些位置的选择并没有特别的惯例，因此本教程将会使用 ~/.config/systemd/user/。

因此，将下面的单元配置存储到文件
`~/.config/systemd/user/python_demo_service.service`中：

    [Unit]
    # Description是用户可读的服务单元名称
    Description=Python Demo Service

当你写好这个文件后，systemd就会找到这个服务：

    $ systemctl --user list-unit-files | grep python_demo_service
    python_demo_service.service         static

服务单元的配置会被记录在[systemd.service]。

### 将服务与python脚本程序关联

现在我们可以开始写我们的python代码作为服务运行脚本。这里我们写一个非常简单的脚本，每隔5秒打印一个条消息。将下面的python脚本保存到`python_demo_service.py`文件中，该文件的存储位置可以自己选择：

    if __name__ == '__main__':
        import time

        while True:
            print('Hello from the Python Demo Service')
            time.sleep(5)

To link our service to our script, extend the unit file as follows:

要将我们的服务脚本和服务单元关联起来，我们需要按照下面的方式扩展我们的服务单元文件：

    [Unit]
    Description=Python Demo Service

    [Service]
    # ExecStart 设定服务启动的命令
    ExecStart=/usr/bin/python path/to/your/python_demo_service.py

### 手动启动和停止服务

现在我们的服务可以手动启动：

    $ systemctl --user start python_demo_service

注意这个命令会立即返回。这是因为systemd会创建一个独立的进程来运行上面的python脚本。这表明我们不需要关注[那些令人厌烦的将python脚本运行正确的fork到守护精灵进程的细节](https://www.python.org/dev/peps/pep-3143/#correct-daemon-behaviour)，因为systemd已经帮我们处理了所有需要的工作。

我们可以检查一下服务的状态：

    $ systemctl --user status python_demo_service
    ● python_demo_service.service - Python Demo Service
       Loaded: loaded (/home/username/.config/systemd/user/python_demo_service.service; static; vendor preset: enabled)
       Active: active (running) since So 2018-12-30 17:46:03 CET; 2min 35s ago
     Main PID: 26218 (python)
       CGroup: /user.slice/user-1000.slice/user@1000.service/python_demo_service.service
               └─26218 /usr/bin/python /home/username/projects/python-systemd-tutorial/python_demo_service.py

在输出结果的第一行，我们可以看到我们在单元文件中设置的`Description`，后续输出结果中还会告诉我们服务的状态和运行的进程PID。

手动停止服务：

    $ systemctl --user stop python_demo_service
    $ systemctl --user status python_demo_service
    ● python_demo_service.service - Python Demo Service
       Loaded: loaded (/home/username/.config/systemd/user/python_demo_service.service)
       Active: inactive (dead)

### STDOUT标准输出和STDERR标准错误

你可能已经发现python脚本的`print`输出并没有出现在终端上。这是因为systemd解绑了服务进程和终端的关联，并且把进程的`STDOUT标准输出`和`STDERR标准错误`重定向了。

对于python来说，需要注意的是STDOUT和STDERR是缓冲了的。当在终端中运行时，这意味着输出只会在出现了一个换行(`\n`)之后才会写入。然而，服务的STDOUT和STDERR都是管道，因此在这种情况下，只有当缓冲区满了的时候才会刷新。所以只有当这个服务产生了足够多的输出时，才会出现在systemd的日志当中。

为了避免这种情况，我们需要禁用STDOUT和STDERR的缓冲特性，[这里有一种可行的方案](https://stackoverflow.com/q/107705/857390)可以实现这个目标。就是设置`PYTHONUNBUFFERED`环境变量，在单元文件中，我们可以通过在`[Servie]`小节当中设置`Enviroment`环境变量来实现：

    Environment=PYTHONUNBUFFERED=1

当你修改了单元文件之后，你需要让systemd重新加载配置，并且重新启动服务：

    $ systemctl --user daemon-reload
    $ systemctl --user restart python_demo_service

服务的输出现在将会立刻出现在systemd的日志当中，默认会重定向到syslog：

    $ grep 'Python Demo Service' /var/log/syslog
    Dec 30 18:05:34 leibniz python[26218]: Hello from the Python Demo Service

另一个查看服务日志的命令是：

    $ journalctl --user-unit python_demo_service

There are many more possible configurations for logging. For example, you can redirect STDOUT and STDERR to files instead. See [systemd.exec] for details.

还有许多可能的对于日志的配置选项。例如，你可以重定向STDOUT和STDERR到文件当中。详情参考[systemd.exec]。

### 启动时自动启动服务

许多服务都设计为系统启动是自动开始运行。对于systemd来说，这是比较简单的。首先我们需要将服务绑定在一个合适的*目标*上：目标是一些特殊的systemd单元，用于将其他的单元组合在一起，并且在启动时实现同步启动。关于目标的通用细节参考[systemd.target]，内建的目标列表参考[systemd.special]。

对于用户服务来说，`default.target`通常是一个目标的好选择，将下面的内容加入单元文件：

    [Install]
    WantedBy=default.target:

然后我们的服务就已经准备好随着系统自动启动了，但是我们还需要*激活enable*这个服务：

    $ systemctl --user enable python_demo_service
    Created symlink from /home/username/.config/systemd/user/default.target.wants/python_demo_service.service to /home/username/.config/systemd/user/python_demo_service.service.

如果你重启你的系统，你就会发现当你登入你的账户时，这个服务已经自动启动了。在你最后一个会话关闭之后，这个服务也会随之被停止。你可以将你的用户systemd实例和你的用户会话分离（这样的话，服务将会随着系统启动而自动启动甚至不需要登入账户，也不会随着你的最后一个会话结束而停止），通过命令：

    $ sudo loginctl enable-linger $USER

禁用自动启动，只需要disable服务：

    $ systemctl --user disable python_demo_service
    Removed symlink /home/username/.config/systemd/user/default.target.wants/python_demo_service.service.

注意，激活一个服务不会立刻启动服务，只会设置服务自动启动，同样的，禁用一个服务不会立刻停止服务，只会设置服务不再自动启动。如果你需要立刻启动/停止服务，你仍旧需要手动启动和停止它，[如上所述](#manually-starting-and-stopping-the-service)。

检查服务的激活状态，使用：

    $ systemctl --user list-unit-files | grep python_demo_service
    python_demo_service.service         enabled

### 发生错误时自动重启服务

对于任何软件来说，你的服务都有可能崩溃。在那种情况下，systemd可以自动尝试重启它。默认情况下，systemd不会这样做，因此你需要在单元文件中激活这个特性。

systemd有许多选项用于精确定义在何种环境下，你的服务会重启。本教程使用一个好的开始点，设置`Restart=on-failure`，将其写入在`[Service]`小节中：

    [Service]
    ...
    Restart=on-failure

这个配置告诉systemd在服务使用了非0返回码退出时重启服务。其他的一些`Restart`的配置可参考[systemd.service]。和其他情况一样，你需要使用`systemctl --user daemon-reload`来刷新systemd的配置。

我们可以使用一个`SIGKILL`信号来模拟服务的崩溃：

    $ systemctl --user --signal=SIGKILL kill python_demo_service

然后，可以在日志中查看到systemd重启了我们的服务：

    $ journalctl --user-unit python_demo_service
    [...]
    Jan 31 12:55:24 leibniz python[3074]: Hello from the Python Demo Service
    Jan 31 12:55:29 leibniz python[3074]: Hello from the Python Demo Service
    Jan 31 12:55:32 leibniz systemd[1791]: python_demo_service.service: Main process exited, code=killed, status=9/KILL
    Jan 31 12:55:32 leibniz systemd[1791]: python_demo_service.service: Unit entered failed state.
    Jan 31 12:55:32 leibniz systemd[1791]: python_demo_service.service: Failed with result 'signal'.
    Jan 31 12:55:33 leibniz systemd[1791]: python_demo_service.service: Service hold-off time over, scheduling restart.
    Jan 31 12:55:33 leibniz systemd[1791]: Stopped Python Demo Service.
    Jan 31 12:55:33 leibniz systemd[1791]: Started Python Demo Service.
    Jan 31 12:55:33 leibniz python[3089]: Hello from the Python Demo Service
    Jan 31 12:55:38 leibniz python[3089]: Hello from the Python Demo Service
    [...]

### 当服务启动完成通知systemd

很多情况下，一个服务需要执行初始化的工作才能真正提供服务的工作。你的服务可以在初始化完成时通知systemd。这种做法对于其他服务依赖与这个服务时特别重要，因为这样可以允许systemd延迟启动其他服务直至你的服务启动完成。

通知是通过[sd_notify]系统调用进行的。我们使用[python-systemd]包来实现，使用之前[确认包安装完成](https://github.com/systemd/python-systemd#installation)。然后在我们的python脚本中加入下列代码：

    if __name__ == '__main__':
        import time
        import systemd.daemon

        print('Starting up ...')
        time.sleep(10)
        print('Startup complete')
        systemd.daemon.notify('READY=1')

        while True:
            print('Hello from the Python Demo Service')
            time.sleep(5)

同时我们也需要将我们的服务类型从`simple`（我们之前使用的默认类型）调整为`notify`。在单元文件`[Service]`小节中加入下面的配置：

    Type=notify

然后使用`systemcrl --user daemon-reload`重新加载配置。

当你重新启动服务的时候，你会看到通知的作用，`systemcrl`不再立刻返回，而是会等待服务的通知才返回。

    $ systemctl --user restart python_demo_service

你可以在[sd_notify]中查看更多的参考资料。
、
## 创建一个系统服务

一旦你已经有了一个用户服务，你可以将它转为系统服务。需要注意的是，系统服务运行在systemd的中心实例上，因此它有更大的机会影响到系统的稳定性和安全性，特别是你的服务的实现不正确时。在很多情况下，这一章节的步骤并不需要，使用用户服务足够。

### 停止和禁用用户服务

在将我们的服务转为系统服务之前，我们需要确认该服务已经停止和禁用了。否则我们可能会既启动了一个用户服务又启动了一个系统服务。

    $ systemctl --user stop python_demo_service
    $ systemctl --user disable python_demo_service

### 移动单元文件

之前，我们将单元文件存储在用户主目录下(`~/.config/systemd/user/`)，这是适合用户服务存储的位置。你可以在[存储目录列表](https://www.freedesktop.org/software/systemd/man/systemd.unit.html#System%20Unit%20Search%20Path)中查看所有systemd扫描的服务单元文件路径。本教程将使用`/etc/systemd/system`作为系统服务单元文件的存储位置，因此将你的单元文件移动到该目录下并设置相应的权限。

    $ sudo mv ~/.config/systemd/user/python_demo_service.service /etc/systemd/system/
    $ sudo chown root:root /etc/systemd/system/python_demo_service.service
    $ sudo chmod 644 /etc/systemd/system/python_demo_service.service

我们的服务现在已经变成了一个系统服务了！这也意味着我们无法使用`systemctl --user ...`命令来控制服务，相应的，我们需要使用`systemctl ...`(不带`--user`参数，且是root用户)或者`sudo systemctl ...`命令（非root用户）。例如：

    $ systemctl list-unit-files | grep python_demo_service
    python_demo_service.service                disabled

同样的，查看日志时需要使用`journalctl --unit ...`命令。

### 移动python脚本

直至目前，你的python脚本应该是存放在你的用户主目录中，对于用户服务来说，这是合适的。但是对于系统服务来说，这不是好的选择。在`/usr/local/lib`中新建一个独立的目录存储python脚本是一个更好的方案：

    $ sudo mkdir /usr/local/lib/python_demo_service
    $ sudo mv ~/path/to/your/python_demo_service.py /usr/local/lib/python_demo_service/
    $ sudo chown root:root /usr/local/lib/python_demo_service/python_demo_service.py
    $ sudo chmod 644 /usr/local/lib/python_demo_service/python_demo-service.py

然后我们应该修改我们的单元文件，将`ExecStart`配置只想新的python脚本的位置

    ExecStart=/usr/bin/python /usr/local/lib/python_demo_service/python_demo_service.py

当然，需要使用`sudo systemctl daemon-reload`重新加载单元文件配置。

### 使用新分配的用户作为服务用户

系统服务默认使用`root`用户运行，这是会带来安全风险的。我们应该分配一个用户专门用户执行这个系统服务，这样我们就可以使用通常的安全性机制（如文件权限）来控制我们的服务能够访问或禁止访问哪些资源。

创建用户用户名的选择最好就和服务名称一致。我们可以使用[useradd]命令创建用户：

    $ sudo useradd -r -s /bin/false python_demo_service

当你创建好用户后，在单元文件`[Service]`小节中加入下面的配置：

    User=python_demo_service

当你重新加载了systemd的配置并且重启了服务之后，你可以查看服务是否使用了正确的用户运行：

    $ sudo systemctl daemon-reload
    $ sudo systemctl restart python_demo_service
    $ sudo systemctl --property=MainPID show python_demo_service
    MainPID=18570
    $ ps -o uname= -p 18570
    python_demo_service

## 进阶内容

We now have a basic implementation of a system systemd service in Python. Depending on your goal, there are many ways to go forward. Here are some ideas:

我们现在已经实现了一个python系统服务。基于你的目标，进阶的方向有很多，下面列出了其中一些：

* Add support for reloading the service's configuration without a hard restart. See the [`ExecReload`](https://www.freedesktop.org/software/systemd/man/systemd.service.html#ExecReload=) option.
* Explore the other features of the [python-systemd] package, for example the [`systemd.journal`](https://www.freedesktop.org/software/systemd/python-systemd/journal.html) module for advanced interaction with the systemd journal.

当然，如果你发现了教程中的错误或者你有补充的内容，欢迎在原作者的repo下面创建一个[issue](https://github.com/torfsen/python-systemd-tutorial/issues)或[pull request](https://github.com/torfsen/python-systemd-tutorial/pulls)。

编程快乐！


[python-systemd]: https://github.com/systemd/python-systemd
[sd_notify]: https://www.freedesktop.org/software/systemd/man/sd_notify.html
[systemd]: https://www.freedesktop.org/wiki/Software/systemd/
[systemd.directives]: https://www.freedesktop.org/software/systemd/man/systemd.directives.html
[systemd.exec]: https://www.freedesktop.org/software/systemd/man/systemd.exec.html
[systemd.unit]: https://www.freedesktop.org/software/systemd/man/systemd.unit.html
[systemd.service]: https://www.freedesktop.org/software/systemd/man/systemd.service.html
[systemd.special]: https://www.freedesktop.org/software/systemd/man/systemd.special.html
[systemd.target]: https://www.freedesktop.org/software/systemd/man/systemd.target.html
[useradd]: https://linux.die.net/man/8/useradd
