"""The app logo (design/logo-preview-v5.html, option I): an FC-style capsule
gamepad drawn in white strokes -- a cross on the left, two round buttons on the
right (the right one orange) -- on solid primary blue.

Run: python -I design/logo.py
Writes every place the logo lives in this repo:
  app/src/main/res/drawable-v24/ic_launcher_foreground.xml  adaptive icon foreground
  app/src/main/res/drawable/ic_launcher_background.xml      adaptive icon background
  app/src/main/res/mipmap-*/ic_launcher{,_round}.png        Android 7.1 and older
  app/src/main/res/drawable/bh_logo.xml                     home page header
  app/src/main/res/drawable/bh_ic_notify.xml                notification silhouette
  website/assets/logo.svg
and prints the inline SVG glyph used by the design boards and the PC app
(bluehandle-go: frontend/index.html, design/pc-preview.html, site/logo.svg).
The PC icons are drawn from the same numbers in bluehandle-go/cmd/bluehandle/build/make_icons.py.

Geometry is in the 108-unit adaptive-icon canvas; launchers show 18..90.
"""
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parent.parent
RES = ROOT / "app" / "src" / "main" / "res"

BLUE = "#2F6FEB"
ORANGE = "#F5A524"
STROKE = 5
# capsule outline (stroke centre line): x 22..86, y 39..69, fully round ends
BODY = (22, 39, 86, 69)
BODY_PATH = "M37,39H71A15,15 0 0,1 71,69H37A15,15 0 0,1 37,39z"
# cross: centre (38, 54), half-length 6.5
CROSS = (38, 54, 6.5)
CROSS_PATH = "M31.5,54H44.5M38,47.5V60.5"
# buttons: B (white) and A (orange)
KEYS = ((64, 54), (74, 54))
KEY_R = 3.8
# the drawing above is 69 units wide; shrink it about the canvas centre so the
# round ends stay inside the 66-unit launcher safe zone. Legacy PNGs and the
# PC icon have no mask, so they get a little more margin.
SCALE = 0.9
RASTER_SCALE = 0.8

HEADER = '<?xml version="1.0" encoding="utf-8"?>\n'
NS = 'xmlns:android="http://schemas.android.com/apk/res/android"'


def circle_path(x, y, r):
    return f"M{x},{y - r}a{r},{r} 0 1,1 0,{2 * r}a{r},{r} 0 1,1 0,{-2 * r}z"


def vector_glyph(indent, white="#FFFFFF", orange=ORANGE):
    """The glyph as VectorDrawable paths in 108-unit canvas coordinates."""
    g = " " * indent
    i = g + "    "
    (bx, by), (ax, ay) = KEYS
    return (f'{g}<group android:scaleX="{SCALE}" android:scaleY="{SCALE}" android:pivotX="54" android:pivotY="54">\n'
            f'{i}<path android:strokeColor="{white}" android:strokeWidth="{STROKE}"\n'
            f'{i}    android:strokeLineJoin="round" android:pathData="{BODY_PATH}" />\n'
            f'{i}<path android:strokeColor="{white}" android:strokeWidth="{STROKE}"\n'
            f'{i}    android:strokeLineCap="round" android:pathData="{CROSS_PATH}" />\n'
            f'{i}<path android:fillColor="{white}" android:pathData="{circle_path(bx, by, KEY_R)}" />\n'
            f'{i}<path android:fillColor="{orange}" android:pathData="{circle_path(ax, ay, KEY_R)}" />\n'
            f'{g}</group>\n')


def svg_glyph():
    x0, y0, x1, y1 = BODY
    (bx, by), (ax, ay) = KEYS
    m = 54 * (1 - SCALE)
    return (f'<g transform="matrix({SCALE} 0 0 {SCALE} {m:.1f} {m:.1f})">'
            f'<rect x="{x0}" y="{y0}" width="{x1 - x0}" height="{y1 - y0}" rx="{(y1 - y0) // 2}" '
            f'fill="none" stroke="#fff" stroke-width="{STROKE}"/>'
            f'<path d="{CROSS_PATH.replace(",", " ")}" stroke="#fff" stroke-width="{STROKE}" stroke-linecap="round"/>'
            f'<circle cx="{bx}" cy="{by}" r="{KEY_R}" fill="#fff"/>'
            f'<circle cx="{ax}" cy="{ay}" r="{KEY_R}" fill="{ORANGE}"/></g>')


def write(path, text):
    path.write_text(text, encoding="utf-8", newline="\n")


def android_vectors():
    write(RES / "drawable-v24" / "ic_launcher_foreground.xml", HEADER +
          "<!-- 启动图标前景：线条勾勒的 FC 式胶囊手柄（design/logo-preview-v5.html 方案 I），由 design/logo.py 生成 -->\n"
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
    # notification icons are drawn by alpha only; everything is white
    write(RES / "drawable" / "bh_ic_notify.xml", HEADER +
          "<!-- 通知栏小图标：logo 剪影，系统只取透明度。由 design/logo.py 生成 -->\n"
          f'<vector {NS}\n    android:width="24dp"\n    android:height="24dp"\n'
          '    android:viewportWidth="72"\n    android:viewportHeight="72">\n'
          '    <!-- 图形以 (54,54) 为中心放进 72×72 -->\n'
          '    <group android:translateX="-18" android:translateY="-18">\n'
          + vector_glyph(8, orange="#FFFFFFFF", white="#FFFFFFFF") + "    </group>\n</vector>\n")


def draw(size, shape):
    """Raster version for legacy launchers."""
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
    im.paste(Image.new("RGBA", (S, S), BLUE), (0, 0), mask)
    d = ImageDraw.Draw(im)
    k = S / 72

    def p(x, y):
        return (54 + (x - 54) * RASTER_SCALE - 18) * k, (54 + (y - 54) * RASTER_SCALE - 18) * k

    k2 = k * RASTER_SCALE
    w = STROKE * k2
    x0, y0, x1, y1 = BODY
    (px0, py0), (px1, py1) = p(x0, y0), p(x1, y1)
    d.rounded_rectangle((px0 - w / 2, py0 - w / 2, px1 + w / 2, py1 + w / 2),
                        radius=(py1 - py0 + w) / 2, outline="white", width=round(w))
    cx, cy, h = CROSS
    for a, b in (((cx - h, cy), (cx + h, cy)), ((cx, cy - h), (cx, cy + h))):
        d.line((*p(*a), *p(*b)), fill="white", width=round(w))
        for e in (a, b):  # round caps
            ex, ey = p(*e)
            d.ellipse((ex - w / 2, ey - w / 2, ex + w / 2, ey + w / 2), fill="white")
    for (x, y), colour in zip(KEYS, ("white", ORANGE)):
        kx, ky = p(x, y)
        r = KEY_R * k2
        d.ellipse((kx - r, ky - r, kx + r, ky + r), fill=colour)
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
    print("svg glyph for other files (108-unit canvas, use viewBox=\"18 18 72 72\"):\n" + svg_glyph())
