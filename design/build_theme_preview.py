"""Build theme-preview.html: every board of ui-preview.html in dark and light.

Run: python -I design/build_theme_preview.py
The light boards are the dark ones with each colour token swapped, so the
mapping below is the proposed light palette (colors_bh.xml in values/,
the current dark values move to values-night/).
"""
import re
from pathlib import Path

HERE = Path(__file__).parent
SRC = HERE / "ui-preview.html"
OUT = HERE / "theme-preview.html"

# token: (dark, light, note)
TOKENS = [
    ("bh_bg", "#0B1220", "#F4F6FA", "页面底色"),
    ("bh_surface", "#141D2E", "#FFFFFF", "卡片、按钮底"),
    ("bh_surface_raised", "#1C2740", "#E9EEF6", "按下、分段选中、图标底"),
    ("bh_surface_selected", "#16223A", "#EAF1FE", "选中的卡片"),
    ("bh_border", "#27334D", "#DCE2EC", "卡片描边"),
    ("bh_divider", "#1F2A40", "#E9EDF3", "分隔线"),
    ("bh_border_dashed", "#3A4866", "#B8C2D4", "虚线框"),
    ("bh_text", "#E8EDF7", "#141A26", "主要文字"),
    ("bh_text_secondary", "#9AA6BD", "#5B6680", "次要文字"),
    ("bh_primary", "#2F6FEB", "#2F6FEB", "主色，不变"),
    ("bh_primary_text", "#8AB8FF", "#1F5BD6", "蓝色文字、图标"),
    ("bh_on_primary_secondary", "#D6E4FF", "#D6E4FF", "主色上的次要文字，不变"),
    ("bh_action", "#F5A524", "#F5A524", "动作橙，不变"),
    ("bh_on_action", "#1A1206", "#1A1206", "橙底上的文字，不变"),
    ("bh_action_surface", "#2A2112", "#FFF3DC", "橙色浅底"),
    ("bh_action_border", "#5A4318", "#F0D199", "橙色浅底描边"),
    ("bh_action_muted", "#D9A54A", "#B26B00", "橙色文字"),
    ("bh_status_connected", "#4C9AFF", "#1F6FE0", "已连接状态"),
    ("bh_switch_off", "#33405C", "#C9D1DE", "开关关闭"),
    ("(滑块轨道)", "#4A5878", "#C3CBD8", "未选中的单选圈、滑块轨道"),
    ("(滑块圆点)", "#6B7896", "#8E99AD", "禁用的滑块圆点"),
]
MAP = {d.upper(): l for _, d, l, _ in TOKENS}
HEX = re.compile(r"#[0-9A-Fa-f]{6}\b")

APPEARANCE = '''
  <div style="font-size: 13px; font-weight: 500; color: #9AA6BD; padding: 6px 4px 0">外观</div>
  <div style="background: #141D2E; border: 1px solid #27334D; border-radius: 18px; overflow: hidden">
    <div style="padding: 14px 16px; display: flex; flex-direction: column; gap: 10px">
      <div style="font-size: 15px">主题</div>
      <div style="display: flex; background: #0B1220; border-radius: 12px; padding: 4px; gap: 4px">
        <button aria-pressed="{follow}" style="flex: 1; height: 38px; border: none; border-radius: 9px; background: {bg_follow}; color: {fg_follow}; font-size: 14px; cursor: pointer">跟随系统</button>
        <button aria-pressed="{light}" style="flex: 1; height: 38px; border: none; border-radius: 9px; background: {bg_light}; color: {fg_light}; font-size: 14px; cursor: pointer">浅色</button>
        <button aria-pressed="{dark}" style="flex: 1; height: 38px; border: none; border-radius: 9px; background: {bg_dark}; color: {fg_dark}; font-size: 14px; cursor: pointer">深色</button>
      </div>
      <div style="font-size: 12px; color: #9AA6BD; line-height: 1.6">Android 10 以下没有系统深色模式，“跟随系统”时显示浅色</div>
    </div>
  </div>
'''


def appearance(selected):
    on, off = ("#2F6FEB", "#FFFFFF; font-weight: 700"), ("transparent", "#9AA6BD")
    v = {}
    for k in ("follow", "light", "dark"):
        bg, fg = on if k == selected else off
        v[k] = "true" if k == selected else "false"
        v["bg_" + k], v["fg_" + k] = bg, fg
    return APPEARANCE.format(**v)


def lighten(html):
    return HEX.sub(lambda m: MAP.get(m.group(0).upper(), m.group(0)), html)


def main():
    src = SRC.read_text(encoding="utf-8")
    head = src[: src.index("<body>")]
    boards = dict(re.findall(r'<section class="board" id="(\w+)">(.*?)</section>', src, re.S))
    order = ["Main", "Settings", "TvRemote", "TvVoice", "TvNumpad", "Ppt", "GamepadScheme", "KeyEdit", "Gamepad"]

    css = '''<style>
.pair { display: flex; gap: 32px; flex-wrap: wrap; align-items: flex-start; padding: 24px; border-radius: 24px; background: #DDE2EA; }
.light .frame { box-shadow: 0 12px 40px rgba(20,26,38,.12); }
.light .frame a { color: #1F5BD6; }
.tokens { border-collapse: collapse; font-size: 13px; margin-top: 16px; background: #fff; border-radius: 12px; overflow: hidden; }
.tokens th, .tokens td { padding: 7px 14px; text-align: left; border-bottom: 1px solid #E3E7EE; }
.tokens th { background: #F1F3F7; font-weight: 500; color: #4A5468; }
.sw { display: inline-block; width: 18px; height: 18px; border-radius: 5px; vertical-align: -4px; margin-right: 6px; border: 1px solid rgba(0,0,0,.12); }
code { font-family: Consolas, monospace; }
</style>
'''
    rows = "".join(
        f'<tr><td><code>{n}</code></td><td><span class="sw" style="background:{d}"></span><code>{d}</code></td>'
        f'<td><span class="sw" style="background:{l}"></span><code>{l}</code></td><td>{note}</td></tr>'
        for n, d, l, note in TOKENS)

    parts = [head.replace("</head>", css + "</head>"), "<body>\n<header>\n",
             "<h1>蓝色手柄 · 浅色主题预览</h1>\n",
             "<p>左边是现在的深色，右边是浅色。浅色由同一套颜色变量换值得到：主色蓝、动作橙不变，"
             "背景、卡片、文字、描边换成浅色。设置页新增「外观」：跟随系统（默认）/ 浅色 / 深色，"
             "所有页面（含游戏手柄、PPT）都跟随这项设置。</p>\n",
             f'<table class="tokens"><tr><th>颜色变量</th><th>深色（现在）</th><th>浅色（新增）</th><th>用途</th></tr>{rows}</table>\n',
             "</header>\n<main class=\"canvas\" style=\"flex-direction: column\">\n"]
    for bid in order:
        dark = boards[bid]
        if bid == "Settings":
            dark = dark.replace("1560px", "1700px").replace("1560", "1700")
            anchor = '  <div style="font-size: 13px; font-weight: 500; color: #9AA6BD; padding: 6px 4px 0">连接</div>'
            assert anchor in dark
            dark_d = dark.replace(anchor, appearance("follow") + "\n" + anchor.replace("6px 4px 0", "10px 4px 0"), 1)
            dark_l = dark.replace(anchor, appearance("follow") + "\n" + anchor.replace("6px 4px 0", "10px 4px 0"), 1)
        else:
            dark_d = dark_l = dark
        light = lighten(dark_l)
        light = re.sub(r'href="#(\w+)"', r'href="#\1-light"', light)
        light = re.sub(r'(<div class="label">)(.*?)(<span>)', r'\1\2 · 浅色\3', light, count=1, flags=re.S)
        dark_d = re.sub(r'(<div class="label">)(.*?)(<span>)', r'\1\2 · 深色\3', dark_d, count=1, flags=re.S)
        parts.append('<div class="pair">\n')
        parts.append(f'<section class="board" id="{bid}">{dark_d}</section>\n')
        parts.append(f'<section class="board light" id="{bid}-light">{light}</section>\n')
        parts.append("</div>\n")
    parts.append("</main>\n</body>\n</html>\n")
    OUT.write_text("".join(parts), encoding="utf-8", newline="\n")
    print(OUT)


if __name__ == "__main__":
    main()
