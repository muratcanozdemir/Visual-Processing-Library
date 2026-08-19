"""Port of ``vpt.algorithms.io.LoadRAW``: reads a plain-text file of
comma-separated integer pixel values (one row per line) into a
grayscale ``ByteImage``, width taken from the first line's token count.

**Preserved quirk**: the Java wraps only the file-reading loop in a bare
``catch (Exception e) { e.printStackTrace(); }`` -- a missing/unreadable
file leaves ``pixels`` empty and execution continues regardless, so the
very next line (``pixels.get(0)``) throws an uncaught
``IndexOutOfBoundsException`` that isn't a ``GlobalException`` and so
isn't swallowed by ``invoke``'s catch block either -- it propagates out
and crashes the call entirely. Reproduced the same way: the read is
wrapped in its own ``try/except Exception``, printed and continued past,
and ``pixels[0]`` is left to raise ``IndexError`` naturally on an empty
list.

Tokenised through ``_javacompat.jsplit`` rather than plain ``str.split``:
Java's ``String.split(regex)`` (no limit argument) drops *trailing* empty
strings (``"1,2,".split(",") == ["1", "2"]``) and special-cases an empty
input string to ``[""]`` rather than `[]`; Python's ``str.split`` does
neither. A stray trailing comma would otherwise turn into a spurious
``int("")`` failure Java wouldn't have hit; a genuinely blank line (empty
after stripping the line terminator) needs to hit that same
``int("")`` failure, not silently become zero tokens."""

from __future__ import annotations

import traceback

from ..._javacompat import jsplit
from ...Algorithm import Algorithm
from ...ByteImage import ByteImage
from ...GlobalException import GlobalException
from ...Image import Image


class LoadRAW(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.path: str | None = None
        self.inputFields = "path"
        self.outputFields = "output"

    def execute(self) -> None:
        pixels: list[list[int]] = []
        try:
            with open(self.path) as f:
                for line in f:
                    line = line.rstrip("\n").rstrip("\r")
                    tokens = jsplit(line, ",")
                    pixels.append([int(t) for t in tokens])
        except Exception:
            traceback.print_exc()

        height = len(pixels)
        width = len(pixels[0])

        self.output = ByteImage(width, height)
        self.output.fill(0)
        for y in range(height):
            line = pixels[y]
            for x in range(width):
                self.output.setXYByte(x, y, line[x])

    @staticmethod
    def invoke(path: str) -> Image | None:
        try:
            return LoadRAW().preprocess(path)
        except GlobalException:
            traceback.print_exc()
        return None
