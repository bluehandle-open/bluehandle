"""The app logo (design/logo-preview-v2.html, option B): a flat D-pad with two
signal arcs in its top-right corner, on solid primary blue.

Run: python -I design/logo.py
Writes every place the logo lives in this repo:
  app/src/main/res/drawable-v24/ic_launcher_foreground.xml  adaptive icon foreground
  app/src/main/res/drawable/ic_launcher_background.xml      adaptive icon background
  app/src/main/res/mipmap-*/ic_launcher{,_round}.png        Android 7.1 and older
  app/src/main/res/drawable/bh_logo.xml                     home page header
  app/src/main/res/drawable/bh_ic_notify.xml                notification silhouette
  website/assets/logo.svg
The PC app draws the same shapes in bluehandle-go/cmd/bluehandle/build/make_icons.py.

Geometry is in the 108-unit adaptive-icon canvas; launchers show 18..90.
"""
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parent.parent
RES = ROOT / "app" / "src" / "main" / "res"

BLUE = "#2F6FEB"
ORANGE = "#F5A524"
# D-pad: arms 18 wide, centre (50, 58)
CROSS = ("M44,32h12a3,3 0 0,1 3,3v14h14a3,3 0 0,1 3,3v12a3,3 0 0,1 -3,3H59v14a3,3 0 0,1 -3,3H44"
         "a3,3 0 0,1 -3,-3V67H27a3,3 0 0,1 -3,-3V52a3,3 0 0,1 3,-3h14V35a3,3 0 0,1 3,-3z")
DOT = (50, 58, 5.5)
# two arcs around the inner corner of the top and right arms, from -80° to -10°
ARC_CENTRE = (62, 46)
ARCS = ((9, "M63.6,37.1A9,9 0 0,1 70.9,44.4"), (17, "M64.95,29.26A17,17 0 0,1 78.74,43.05"))
ARC_WIDTH = 5.5
# shrink by 0.82 about the canvas centre and nudge to optical centre
SCALE, SHIFT = 0.82, (2, -2)
OFFSET = (54 - 54 * SCALE + SHIFT[0] * SCALE, 54 - 54 * SCALE + SHIFT[1] * SCALE)  # (11.36, 8.08)

HEADER = '<?xml version="1.0" encoding="utf-8"?>\n'
NS = 'xmlns:android="http://schemas.android.com/apk/res/android"'


def dot_path():
    x, y, r = DOT
    return f"M{x},{y - r}a{r},{r} 0 1,1 0,{2 * r}a{r},{r} 0 1,1 0,{-2 * r}z"


def vector_glyph(indent, white="#FFFFFF", orange=ORANGE):
    """The glyph as VectorDrawable paths, inside the scale/offset group."""
    i = " " * indent
    arcs = "".join(p for _, p in ARCS)
    return (f'{i}<group android:translateX="{OFFSET[0]:.2f}" android:translateY="{OFFSET[1]:.2f}"\n'
            f'{i}    android:scaleX="{SCALE}" android:scaleY="{SCALE}">\n'
            f'{i}    <path android:fillColor="{white}" android:pathData="{CROSS}" />\n'
            f'{i}    <path android:fillColor="{orange}" android:pathData="{dot_path()}" />\n'
            f'{i}    <path android:strokeColor="{white}" android:strokeWidth="{ARC_WIDTH}"\n'
            f'{i}        android:strokeLineCap="round" android:pathData="{arcs}" />\n'
            f'{i}</group>\n')


def svg_glyph():
    arcs = " ".join(p.replace(",", " ") for _, p in ARCS)
    x, y, r = DOT
    return (f'<g transform="matrix({SCALE} 0 0 {SCALE} {OFFSET[0]:.2f} {OFFSET[1]:.2f})">'
            f'<path fill="#fff" d="{CROSS.replace(",", " ")}"/>'
            f'<circle cx="{x}" cy="{y}" r="{r}" fill="{ORANGE}"/>'
            f'<path d="{arcs}" fill="none" stroke="#fff" stroke-width="{ARC_WIDTH}" stroke-linecap="round"/></g>')


def write(path, text):
    path.write_text(text, encoding="utf-8", newline="\n")


def android_vectors():
    write(RES / "drawable-v24" / "ic_launcher_foreground.xml", HEADER +
          "<!-- 启动图标前景：方向键 + 信号（design/logo-preview-v2.html 方案 B），由 design/logo.py 生成 -->\n"
          f'<vector {NS}\n    android:width="108dp"\n    android:height="108dp"\n'
          '    android:viewportWidth="108"\n    android:viewportHeight="108">\n'
          + vector_glyph(4) + "</vector>\n")
    write(RES / "drawable" / "ic_launcher_background.xml", HEADER +
          "<!-- 启动图标背景：主色蓝纯色，由 design/logo.py 生成 -->\n"
          f'<shape {NS} android:shape="rectangle">\n    <solid android:color="{BLUE}" />\n</shape>\n')
    # 72-unit viewport = the visible part of the adaptive canvas
    write(RES / "drawable" / "bh_logo.xml", HEADER +
          "<!-- 应用内的 logo（首页顶栏），与启动图标同一造型，由 design/logo.py 生成 -->\n"
          f'<vector {NS}\n    android:width="36dp"\n    android:height="36dp"\n'
          '    android:viewportWidth="72"\n    android:viewportHeight="72">\n'
          f'    <path\n        android:fillColor="{BLUE}"\n'
          '        android:pathData="M16,0h40a16,16 0 0,1 16,16v40a16,16 0 0,1 -16,16H16a16,16 0 0,1 -16,-16V16a16,16 0 0,1 16,-16z" />\n'
          '    <group android:translateX="-18" android:translateY="-18">\n'
          + vector_glyph(8) + "    </group>\n</vector>\n")
    # notification icons are drawn by alpha only: centre dot becomes a hole
    arcs = "".join(p for _, p in ARCS)
    write(RES / "drawable" / "bh_ic_notify.xml", HEADER +
          "<!-- 通知栏小图标：logo 剪影，系统只取透明度。由 design/logo.py 生成 -->\n"
          f'<vector {NS}\n    android:width="24dp"\n    android:height="24dp"\n'
          '    android:viewportWidth="58"\n    android:viewportHeight="58">\n'
          '    <!-- 图形实际范围约 x 24..81.5、y 26.5..84，移到 58×58 正中 -->\n'
          '    <group android:translateX="-23.75" android:translateY="-26.25">\n'
          f'        <path android:fillColor="#FFFFFFFF" android:fillType="evenOdd"\n'
          f'            android:pathData="{CROSS}{dot_path()}" />\n'
          f'        <path android:strokeColor="#FFFFFFFF" android:strokeWidth="{ARC_WIDTH}"\n'
          f'            android:strokeLineCap="round" android:pathData="{arcs}" />\n'
          "    </group>\n</vector>\n")


def draw(size, shape):
    """Raster version for legacy launchers and the PC app."""
    S = 1024
    im = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    mask = Image.new("L", (S, S), 0)
    inset = S * 0.04 if shape != "full" else 0
    box = (inset, inset, S - inset, S - inset)
    md = ImageDraw.Draw(mask)
    if shape == "round":
        md.ellipse(box, fill=255)
    else:
        md.rounded_rectangle(box, radius=S * 0.22 if shape == "full" else S * 0.2, fill=255)
    tile = Image.new("RGBA", (S, S), BLUE)
    im.paste(tile, (0, 0), mask)
    d = ImageDraw.Draw(im)
    k = S / 72

    def p(x, y):
        return ((x * SCALE + OFFSET[0]) - 18) * k, ((y * SCALE + OFFSET[1]) - 18) * k

    def r(v):
        return v * SCALE * k

    rr = r(3)
    for x0, y0, x1, y1 in ((41, 32, 59, 84), (24, 49, 76, 67)):
        d.rounded_rectangle((*p(x0, y0), *p(x1, y1)), radius=rr, fill="white")
    cx, cy = p(DOT[0], DOT[1])
    d.ellipse((cx - r(DOT[2]), cy - r(DOT[2]), cx + r(DOT[2]), cy + r(DOT[2])), fill=ORANGE)
    ax, ay = p(*ARC_CENTRE)
    w = r(ARC_WIDTH)
    import math
    for radius, _ in ARCS:
        R = r(radius)
        d.arc((ax - R - w / 2, ay - R - w / 2, ax + R + w / 2, ay + R + w / 2), 280, 350, fill="white", width=round(w))
        for deg in (280, 350):  # round caps
            ex, ey = ax + R * math.cos(math.radians(deg)), ay + R * math.sin(math.radians(deg))
            d.ellipse((ex - w / 2, ey - w / 2, ex + w / 2, ey + w / 2), fill="white")
    return im.resize((size, size), Image.LANCZOS)


def android_pngs():
    for name, size in {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}.items():
        out = RES / f"mipmap-{name}"
        draw(size, "square").save(out / "ic_launcher.png")
        draw(size, "round").save(out / "ic_launcher_round.png")


def website():
    write(ROOT / "website" / "assets" / "logo.svg",
          '<svg xmlns="http://www.w3.org/2000/svg" viewBox="18 18 72 72">'
          f'<rect x="18" y="18" width="72" height="72" rx="16" fill="{BLUE}"/>{svg_glyph()}</svg>\n')


if __name__ == "__main__":
    android_vectors()
    android_pngs()
    website()
    print("svg glyph for other files:\n" + svg_glyph())
