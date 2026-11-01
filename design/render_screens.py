"""Render design boards to standalone images for the website.

Run: python -I design/render_screens.py [--phone]
The PC window comes from bluehandle-go's design/pc-preview.html. Phone pages on
the website are real device screenshots (see export_device_shots.py); pass
--phone to overwrite them with renders of design/theme-preview.html instead. Each board is put alone
on a transparent page and captured by headless Edge at 2x, then saved as WebP
in website/assets/screens/.
"""
import re
import subprocess
import sys
import tempfile
from pathlib import Path

from PIL import Image

HERE = Path(__file__).parent
OUT = HERE.parent / "website" / "assets" / "screens"
PC_PREVIEW = Path(r"E:\kuaipan\code\go2\bluehandle-go\design\pc-preview.html")
EDGE = Path(r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe")

PHONE = ["Main", "TvRemote", "TvVoice", "TvNumpad", "Ppt", "GamepadScheme", "KeyEdit", "Gamepad"]
PC = {"pc-connected": 2, "pc-connect": 0}  # name: board index in pc-preview.html


def capture(html: str, width: int, height: int, out: Path) -> None:
    with tempfile.TemporaryDirectory() as tmp:
        page = Path(tmp) / "board.html"
        png = Path(tmp) / "board.png"
        page.write_text(html, encoding="utf-8")
        subprocess.run([str(EDGE), "--headless=new", "--disable-gpu", "--hide-scrollbars",
                        "--force-device-scale-factor=2", "--default-background-color=00000000",
                        f"--window-size={width},{height}", f"--screenshot={png}", page.as_uri()],
                       check=True, capture_output=True, timeout=60)
        Image.open(png).save(out, "WEBP", quality=88, method=6)


def alone(head: str, board: str) -> str:
    return head + '<body style="margin:0;background:transparent;overflow:hidden">' + board + "</body></html>"


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    src = (HERE / "theme-preview.html").read_text(encoding="utf-8")
    head = src[: src.index("<body>")]
    for bid in PHONE if "--phone" in sys.argv else []:
        for suffix in ("", "-light"):
            m = re.search(rf'<section class="board[^"]*" id="{bid}{suffix}">(.*?)</section>', src, re.S)
            body = re.sub(r'<div class="label">.*?</div>', "", m.group(1), count=1, flags=re.S)
            w, h = map(int, re.search(r'width:\s*(\d+)px;\s*height:\s*(\d+)px', body).groups())
            body = body.replace("box-shadow", "x-box-shadow")
            name = f"{bid.lower()}{'-light' if suffix else ''}.webp"
            capture(alone(head, body), w, h, OUT / name)
            print(name)

    pc = PC_PREVIEW.read_text(encoding="utf-8")
    pc_head = pc[: pc.index("<body>")]
    boards = re.findall(r'<section class="board">(.*?)</section>', pc, re.S)
    for name, i in PC.items():
        body = re.sub(r'<div class="label">.*?</div>', "", boards[i], count=1, flags=re.S)
        capture(alone(pc_head, body), 420, 640, OUT / f"{name}.webp")
        print(name)


if __name__ == "__main__":
    sys.exit(main())
