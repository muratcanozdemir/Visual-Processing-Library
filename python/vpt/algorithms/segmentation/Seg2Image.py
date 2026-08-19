"""Port of ``vpt.algorithms.segmentation.berkeley.Seg2Image`` (the Java
file sits in the ``segmentation.berkeley`` subpackage alongside a gamut-
table ``ColorQFZRGB``/``ColorVector`` pair and four ``main()``-only test
harnesses, all excluded -- see ``python/README.md``; this is the one
genuinely portable file in that subpackage, so it joins the flat
``segmentation`` package here rather than a new ``berkeley`` subpackage,
matching how ``io.LoadRAW``/``LoadFLO`` aren't nested under their own
fixture-format subpackage either).

Parses a Berkeley Segmentation Dataset ``.seg`` text file into a labelled
``IntegerImage``: an arbitrary number of ``key value`` header lines (only
``width``/``height`` are read; everything else, e.g. ``format``/``date``/
``user``, is silently ignored) up to a line whose first token is
``data``, followed by one run-length row per line: ``segmentId row col1
col2`` (inclusive), stamping ``segmentId`` across ``[col1, col2]`` on
``row``. Raises if the header never supplied both dimensions, or if the
data rows don't collectively cover every one of ``width*height`` pixels
exactly once.

**Preserved bug, bytecode-confirmed**: the entire body -- header parsing,
row parsing, and the final coverage-count check -- is wrapped in one Java
``catch (Exception e) { throw new GlobalException("Something wrong has
happened...") }``. ``GlobalException extends Exception``, and `javap`'s
exception table shows that one `catch` region spans the *whole* method
body, including the two specific `throw new GlobalException(...)`
statements ("Could not find either width or height", "Missing pixel
value(s)!") -- so those two specific messages are unreachable from the
caller: every failure (a missing file, a malformed row, missing
dimensions, or incomplete coverage) surfaces as the same generic
"Something wrong has happened..." message. Reproduced verbatim: the two
specific `raise GlobalException(...)` calls are lexically inside the
same `try` whose `except Exception` re-wraps them, exactly as in Java --
not hoisted out to let their specific messages actually surface.

Tokenised through ``_javacompat.jsplit`` rather than plain ``str.split``,
same reasoning as ``LoadRAW``/``Algorithm._getFields``: Java's
``String.split(" ")`` (no limit) drops trailing empty tokens and
special-cases an empty line to ``[""]``, which Python's ``str.split``
does not."""

from __future__ import annotations

import traceback

from ..._javacompat import jsplit
from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image
from ...IntegerImage import IntegerImage


class Seg2Image(Algorithm):
    def __init__(self):
        super().__init__()
        self.path: str | None = None
        self.output: Image | None = None
        self.inputFields = "path"
        self.outputFields = "output"

    def execute(self) -> None:
        try:
            with open(self.path) as file:
                width = -1
                height = -1
                for line in file:
                    line = line.rstrip("\n").rstrip("\r")
                    tags = jsplit(line, " ")
                    if tags[0] == "width":
                        width = int(tags[1])
                    elif tags[0] == "height":
                        height = int(tags[1])
                    elif tags[0] == "data":
                        break

                if width == -1 or height == -1:
                    raise GlobalException("Could not find either width or height")

                self.output = IntegerImage(width, height, 1)

                self.output.fill(0)

                cnt = 0
                for line in file:
                    line = line.rstrip("\n").rstrip("\r")
                    tags = jsplit(line, " ")

                    segmentId = int(tags[0])
                    row = int(tags[1])
                    col1 = int(tags[2])
                    col2 = int(tags[3])

                    for x in range(col1, col2 + 1):
                        self.output.setXYInt(x, row, segmentId)
                        cnt += 1

                if cnt != width * height:
                    raise GlobalException("Missing pixel value(s)!")
        except Exception as e:
            raise GlobalException("Something wrong has happened...") from e

    @staticmethod
    def invoke(path: str) -> Image | None:
        try:
            return Seg2Image().preprocess(path)
        except GlobalException:
            traceback.print_exc()
        return None
