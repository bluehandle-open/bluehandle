"""把画布的 .dc.html 设计稿合成为一个可离线打开的本地预览页。"""
import re
import sys
from pathlib import Path

src_dir = Path(sys.argv[1])
out_file = Path(sys.argv[2])
ACCENT = "#2F6FEB"

boards = [
    ("Main", "首页 · 连接", 390, 844),
    ("TvRemote", "电脑遥控器（电视遥控器外观）", 390, 844),
    ("TvNumpad", "遥控器 · 数字键", 390, 844),
    ("TvVoice", "遥控器 · 语音输入", 390, 844),
    ("Ppt", "PPT 演示", 390, 844),
    ("GamepadScheme", "游戏手柄 · 选择键位方案", 390, 844),
    ("KeyEdit", "自定义按键编辑", 390, 844),
    ("Settings", "设置（完整滚动长度）", 390, 1560),
    ("Gamepad", "游戏手柄 · 横屏", 844, 390),
]

sections = []
for name, title, w, h in boards:
    text = (src_dir / f"{name}.dc.html").read_text(encoding="utf-8")
    body = text.split("</helmet>", 1)[1].split("</x-dc>", 1)[0].strip()
    body = body.replace("{{accent}}", ACCENT)
    # 画布内的跳转链接改成页内锚点
    body = re.sub(r'href="(\w+)\.dc\.html"', r'href="#\1"', body)
    sections.append(
        f'<section class="board" id="{name}">\n'
        f'<div class="label">{title}<span>{w} × {h}</span></div>\n'
        f'<div class="frame" style="width:{w}px;height:{h}px">\n{body}\n</div>\n'
        f'</section>'
    )

html = f"""<!doctype html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>蓝色手柄 UI 改版 · 本地预览</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@400;500;700&amp;display=swap" rel="stylesheet">
<style>
:root {{ color-scheme: light; }}
body {{ margin: 0; background: #E9ECF2; font-family: 'Noto Sans SC', system-ui, sans-serif; color: #141A26; }}
header {{ padding: 32px 40px 8px; }}
header h1 {{ margin: 0 0 6px; font-size: 28px; }}
header p {{ margin: 0; color: #4A5468; font-size: 14px; }}
.canvas {{ display: flex; flex-wrap: wrap; gap: 48px; padding: 32px 40px 64px; align-items: flex-start; }}
.board {{ display: flex; flex-direction: column; gap: 10px; }}
.label {{ font-size: 14px; font-weight: 700; display: flex; gap: 10px; align-items: baseline; }}
.label span {{ font-weight: 400; color: #4A5468; font-size: 12px; }}
.frame {{ border-radius: 28px; overflow: hidden; box-shadow: 0 12px 40px rgba(20,26,38,.25); flex-shrink: 0; }}
.frame a {{ color: #8AB8FF; }}
.frame button, .frame input {{ font-family: inherit; }}
</style>
</head>
<body>
<header>
<h1>蓝色手柄 · 新视觉：游戏手柄、遥控器、PPT 三种模式</h1>
<p>本地静态预览，主色 {ACCENT}。点击稿内的返回、数字键等链接会跳到对应画板。</p>
</header>
<main class="canvas">
{chr(10).join(sections)}
</main>
</body>
</html>
"""
out_file.parent.mkdir(parents=True, exist_ok=True)
out_file.write_text(html, encoding="utf-8")
print(out_file)
