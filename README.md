# 蓝色手柄

把手机变成电脑的手柄、遥控器和键盘。手机和电脑通过 Wi‑Fi 或蓝牙直连，不经过任何服务器。

- **游戏手柄**：横屏双手，方向键、A / B / X / Y、Select、Start；支持多指同时按、重力感应，可以给每个游戏建一套自己的键位方案，一个按钮最多 3 个键组合。
- **电脑遥控器**：竖屏单手，方向键、确定、返回、音量、媒体播放、锁屏、回到桌面、切换窗口、翻页，另有数字键盘。把电脑接上电视或投影，躺在沙发上就能操作。
- **PPT 演示**：大号翻页按钮，手机音量键也能翻页；演讲计时，剩余 2 分钟时震动提醒；从当前页放映、黑屏、退出放映一键完成。
- **语音输入**：按住说话，松手后文字直接输入到电脑当前光标处，可以自动按回车。

支持浅色和深色两种主题，默认跟随系统。

## 下载

需要手机端和电脑端都安装：

| | 下载 | 系统要求 |
|---|---|---|
| 手机端 | [GitHub Releases](https://github.com/bluehandle-open/bluehandle/releases/latest) | Android 4.1 及以上 |
| 电脑端 | [GitHub Releases](https://github.com/bluehandle-open/bluehandle-go/releases/latest) | Windows 10 / 11 |

电脑端源码在 [bluehandle-go](https://github.com/bluehandle-open/bluehandle-go)。

## 快速开始

1. 电脑上运行 `bluehandle.exe`。
2. 手机打开蓝色手柄，选择 Wi‑Fi 或蓝牙，点「开始连接」。
   - Wi‑Fi：手机和电脑连同一个网络，在电脑端输入手机首页显示的地址。
   - 蓝牙：先在系统设置里把手机和电脑配对，再在电脑端的「蓝牙」列表里选择这部手机。
3. 连上后在首页选择一种用法。在各模式之间切换、回到首页，连接都不会断开。

完整说明见使用手册 [`website/manual.html`](website/manual.html)。

## 开发

- 编译需要 JDK 17：`./gradlew assembleDebug`
- 手机与电脑之间的通信协议见 [`docs/protocol.md`](docs/protocol.md)，电脑端仓库中有一份相同的文档。
- `website/` 是宣传页与使用手册（纯静态页面），`design/` 是设计稿和生成 logo、截图的脚本。
- 推送 `v*` tag 时，GitHub Actions 会编译、签名 APK 并发布 Release。

## 许可

[MIT](LICENSE)
