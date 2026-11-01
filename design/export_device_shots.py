"""Turn raw device screenshots into the website's phone images.

Run: python -I design/export_device_shots.py <raw_dir> website/assets/screens

<raw_dir> holds `adb exec-out screencap -p` PNGs from a 1080x1920 phone, one set
per theme (switch it in 设置 → 外观), named <page>-<dark|light>.png for
main, gamepadscheme, keyedit, ppt, tvnumpad, tvvoice, gamepad, plus
tvremote-<theme>.png and tvremote-<theme>-bottom.png (the remote scrolled to
its end; the two are stitched). The status bar is cropped and the landscape
gamepad, which screencap returns sideways, is rotated upright.
"""
import os, sys
from PIL import Image, ImageChops

src, dst = sys.argv[1], sys.argv[2]
BAR = 60          # status bar height on the device
PHONE_W = 720     # output width for portrait shots


def load(name):
    return Image.open(os.path.join(src, name + '.png')).convert('RGB')


def find_offset(top, bottom, band=200):
    """Scroll distance between two shots of the same page: find d where bottom[y] == top[y + d]."""
    w, h = top.size
    y0 = 700  # a band from the scrolled shot, well below the status bar
    probe = bottom.crop((0, y0, w, y0 + band))
    best = None
    for d in range(1, h - y0 - band):
        cand = top.crop((0, y0 + d, w, y0 + d + band))
        diff = ImageChops.difference(probe, cand).getbbox()
        if diff is None:
            return d
        s = sum(ImageChops.difference(probe, cand).convert('L').histogram()[8:])
        if best is None or s < best[0]:
            best = (s, d)
    return best[1]


def stitch(top, bottom):
    d = find_offset(top, bottom)
    w, h = top.size
    # page content: top shot from BAR to h, then the part of bottom that was below the top shot
    out = Image.new('RGB', (w, h + d - BAR))
    out.paste(top.crop((0, BAR, w, h)), (0, 0))
    extra = bottom.crop((0, h - d, w, h))
    out.paste(extra, (0, h - BAR))
    print('  stitched offset', d)
    return out


def save(im, name, width):
    if im.width != width:
        im = im.resize((width, round(im.height * width / im.width)), Image.LANCZOS)
    p = os.path.join(dst, name + '.webp')
    im.save(p, 'WEBP', quality=86, method=6)
    print(name, im.size, os.path.getsize(p) // 1024, 'KB')


for theme, suffix in (('dark', ''), ('light', '-light')):
    for page in ('main', 'gamepadscheme', 'keyedit', 'ppt', 'tvnumpad', 'tvvoice'):
        im = load('%s-%s' % (page, theme))
        save(im.crop((0, BAR, im.width, im.height)), page + suffix, PHONE_W)
    save(stitch(load('tvremote-' + theme), load('tvremote-%s-bottom' % theme)), 'tvremote' + suffix, PHONE_W)
    gp = load('gamepad-' + theme).rotate(90, expand=True)
    save(gp, 'gamepad' + suffix, 1440)
