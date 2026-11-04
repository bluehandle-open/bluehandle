"""Store listing graphics under fastlane/metadata/android/<locale>/images/.

Run: python -I design/fastlane_assets.py
Writes, for every locale:
  icon.png                 512x512 full-bleed icon (stores apply their own mask)
  featureGraphic.png       1024x500 banner
  phoneScreenshots/N.png   PNG copies of website/assets/screens/*-light.webp
Screenshots taller than 2:1 are padded sideways with the screen's own
background colour, because Google Play rejects a long side over twice the short
side. The texts next to the images are hand-written; this only draws pixels.
"""
import sys
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parent.parent
sys.dont_write_bytecode = True
sys.path.insert(0, str(ROOT / "design"))
import logo  # noqa: E402  (same folder, run with -I so the path is explicit)

SCREENS = ROOT / "website" / "assets" / "screens"
META = ROOT / "fastlane" / "metadata" / "android"
FONTS = Path("C:/Windows/Fonts")

# listing order: the first two are what most store pages show without scrolling
SHOTS = ["main", "gamepad", "tvremote", "ppt", "tvvoice", "gamepadscheme", "keyedit", "tvnumpad"]

LOCALES = {
    "zh-CN": ("蓝色手柄", "把手机变成电脑的手柄、遥控器和键盘", "msyhbd.ttc", "msyh.ttc"),
    "en-US": ("BlueHandle", "Your phone as a gamepad, remote and keyboard for your PC",
              "segoeuib.ttf", "segoeui.ttf"),
}


def icon():
    im = Image.new("RGBA", (512, 512), logo.BLUE)
    glyph = logo.draw(512, "full")
    im.alpha_composite(glyph)
    return im.convert("RGB")


def max_ratio(im, ratio=2.0):
    w, h = im.size
    long_, short = max(w, h), min(w, h)
    if long_ <= short * ratio:
        return im
    bg = im.getpixel((2, 2))
    if h > w:
        nw = -(-h // 2)
        out = Image.new("RGB", (nw, h), bg)
        out.paste(im, ((nw - w) // 2, 0))
    else:
        nh = -(-w // 2)
        out = Image.new("RGB", (w, nh), bg)
        out.paste(im, (0, (nh - h) // 2))
    return out


def feature(title, subtitle, bold, regular):
    W, H = 1024, 500
    im = Image.new("RGB", (W, H), logo.BLUE)
    d = ImageDraw.Draw(im)
    # phone screenshot on the right, cut off by the bottom edge
    shot = Image.open(SCREENS / "main-light.webp").convert("RGB")
    sw = 300
    shot = shot.resize((sw, round(shot.height * sw / shot.width)), Image.LANCZOS)
    frame = Image.new("RGB", (sw + 24, shot.height + 24), "#1B4FB8")
    mask = Image.new("L", frame.size, 0)
    ImageDraw.Draw(mask).rounded_rectangle((0, 0, *frame.size), radius=36, fill=255)
    frame.paste(shot, (12, 12))
    im.paste(frame, (W - sw - 90, 70), mask)
    # icon + texts on the left
    # opaque full-bleed icon on the same blue, so no anti-aliased square edge shows
    im.paste(icon().resize((150, 150), Image.LANCZOS), (70, 90))
    tf = ImageFont.truetype(str(FONTS / bold), 64)
    sf = ImageFont.truetype(str(FONTS / regular), 28)
    d.text((70, 270), title, font=tf, fill="white")
    # wrap the subtitle to the free width
    words = subtitle.split(" ") if " " in subtitle else list(subtitle)
    sep = " " if " " in subtitle else ""
    lines, cur = [], ""
    for w in words:
        nxt = (cur + sep + w) if cur else w
        if d.textlength(nxt, font=sf) > 500:
            lines.append(cur)
            cur = w
        else:
            cur = nxt
    lines.append(cur)
    for i, line in enumerate(lines):
        d.text((72, 360 + i * 40), line, font=sf, fill="#DCE7FF")
    return im


def main():
    ic = icon()
    shots = [max_ratio(Image.open(SCREENS / f"{n}-light.webp").convert("RGB")) for n in SHOTS]
    for loc, (title, subtitle, bold, regular) in LOCALES.items():
        out = META / loc / "images"
        (out / "phoneScreenshots").mkdir(parents=True, exist_ok=True)
        for old in (out / "phoneScreenshots").glob("*.png"):
            old.unlink()
        ic.save(out / "icon.png", optimize=True)
        feature(title, subtitle, bold, regular).save(out / "featureGraphic.png", optimize=True)
        for i, s in enumerate(shots, 1):
            s.save(out / "phoneScreenshots" / f"{i}.png", optimize=True)
        print(loc, "->", out.relative_to(ROOT))


if __name__ == "__main__":
    main()
