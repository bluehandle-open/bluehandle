"""Draw the launcher icon PNGs (方案 B · 方向键, see design/logo-preview.html).

Run: python -I design/make_logo.py app/src/main/res
Needs Pillow. Writes mipmap-*/ic_launcher.png and ic_launcher_round.png for
Android 7.1 and older; 8.0+ uses the adaptive vector in drawable-v24.
The geometry matches the vectors: 108-unit canvas, glyph scaled 0.78 about
the centre, visible area 18..90.
"""
import sys
from pathlib import Path

from PIL import Image, ImageDraw

TOP = (61, 130, 247)
BOTTOM = (28, 76, 200)
WHITE = (255, 255, 255, 255)
BLUE = (47, 111, 235, 255)
ORANGE = (245, 165, 36, 255)
S = 1024  # drawn large, then downsampled
DENSITIES = {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}


def gradient() -> Image.Image:
    """Diagonal gradient, top-left to bottom-right."""
    small = Image.new("RGB", (64, 64))
    px = small.load()
    for y in range(64):
        for x in range(64):
            t = (x + y) / 126
            px[x, y] = tuple(round(a + (b - a) * t) for a, b in zip(TOP, BOTTOM))
    return small.resize((S, S), Image.BILINEAR).convert("RGBA")


def glyph(im: Image.Image) -> None:
    d = ImageDraw.Draw(im)
    k = S / 72  # visible area is 72 units wide

    def p(x, y):  # 108-unit canvas -> pixels, with the 0.78 glyph scale
        x = 54 + (x - 54) * 0.78
        y = 54 + (y - 54) * 0.78
        return (x - 18) * k, (y - 18) * k

    r = 5 * 0.78 * k
    d.rounded_rectangle((*p(42, 24), *p(66, 84)), radius=r, fill=WHITE)
    d.rounded_rectangle((*p(24, 42), *p(84, 66)), radius=r, fill=WHITE)
    for tri in (((54, 30), (49.5, 35.5), (58.5, 35.5)), ((54, 78), (49.5, 72.5), (58.5, 72.5)),
                ((30, 54), (35.5, 49.5), (35.5, 58.5)), ((78, 54), (72.5, 49.5), (72.5, 58.5))):
        d.polygon([p(*v) for v in tri], fill=BLUE)
    cx, cy = p(54, 54)
    cr = 7.5 * 0.78 * k
    d.ellipse((cx - cr, cy - cr, cx + cr, cy + cr), fill=ORANGE)


def icon(shape: str) -> Image.Image:
    im = gradient()
    glyph(im)
    mask = Image.new("L", (S, S), 0)
    md = ImageDraw.Draw(mask)
    inset = S * 0.04  # legacy launchers expect a little margin around the tile
    box = (inset, inset, S - inset, S - inset)
    if shape == "round":
        md.ellipse(box, fill=255)
    else:
        md.rounded_rectangle(box, radius=S * 0.2, fill=255)
    im.putalpha(mask)
    return im


def main(res: Path) -> None:
    square, round_ = icon("square"), icon("round")
    for name, size in DENSITIES.items():
        out = res / f"mipmap-{name}"
        out.mkdir(parents=True, exist_ok=True)
        square.resize((size, size), Image.LANCZOS).save(out / "ic_launcher.png")
        round_.resize((size, size), Image.LANCZOS).save(out / "ic_launcher_round.png")


if __name__ == "__main__":
    main(Path(sys.argv[1]))
