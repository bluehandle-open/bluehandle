"""Turn raw device screenshots into the website's phone images.

Run: python -I design/export_device_shots.py <raw_dir> website/assets/screens
English: use website/assets/screens/en as the output directory.
Use --status-bar <pixels> for a different status bar height. If the remote
fits in one screen, pass --single-screen-remote to skip stitching.

<raw_dir> holds `adb exec-out screencap -p` PNGs from a 1080x1920 phone, one set
per theme (switch it in 设置 → 外观), named <page>-<dark|light>.png for
main, gamepadscheme, keyedit, ppt, tvnumpad, tvvoice, gamepad, plus
tvremote-<theme>.png and tvremote-<theme>-bottom.png (the remote scrolled to
its end; the two are stitched). The status bar is cropped and the landscape
gamepad, which screencap returns sideways, is rotated upright.
"""
import argparse
import os
from PIL import Image, ImageChops

parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument('src')
parser.add_argument('dst')
parser.add_argument('--status-bar', type=int, default=60)
parser.add_argument('--single-screen-remote', action='store_true')
args = parser.parse_args()
src, dst = args.src, args.dst
os.makedirs(dst, exist_ok=True)
BAR = args.status_bar  # status bar height on the device
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
    remote = load('tvremote-' + theme)
    if args.single_screen_remote:
        remote = remote.crop((0, BAR, remote.width, remote.height))
    else:
        remote = stitch(remote, load('tvremote-%s-bottom' % theme))
    save(remote, 'tvremote' + suffix, PHONE_W)
    gp = load('gamepad-' + theme).rotate(90, expand=True)
    save(gp, 'gamepad' + suffix, 1440)
