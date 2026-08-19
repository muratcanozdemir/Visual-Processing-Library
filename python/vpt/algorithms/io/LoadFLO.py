"""Port of ``vpt.algorithms.io.LoadFLO``: reads a Middlebury ``.flo``
optical-flow file and encodes each pixel's flow direction (not
magnitude) as a normalised ``[0, 1)`` angle in a ``DoubleImage`` --
``(atan2(v, u) + pi) / (2*pi)``.

The Java decodes each little-endian 4-byte field by manually reversing
its bytes into a big-endian ``ByteBuffer`` and reading that; the net
effect is exactly little-endian decoding, so this port just uses
``struct``'s ``<f``/``<i`` directly rather than reproducing the
reverse-then-read-big-endian dance. One real quirk carried over anyway:
``bytes2Integer`` is declared to *return* ``float``, not ``int`` -- so
the raw 4-byte int (`width`/`height`) is implicitly widened to `float`
before the caller narrows it back to `int` with an explicit cast. Routed
through `jfloat`/`jint` for that double round-trip, though for realistic
image dimensions (well under float32's 2^24 exact-integer range) it's
never lossy in practice.

The sanity-check magic number (``202021.25``, the standard `.flo` magic)
and the width/height-nonzero check both raise `GlobalException` on
failure, same as the Java. `debug` is a hardcoded-`False` dead field in
the Java (never set anywhere) gating a handful of stderr `System.err`
prints; the Python equivalent is simply omitted since it can never fire."""

from __future__ import annotations

import math
import struct
import traceback

from ..._javacompat import jatan2, jfloat, jint
from ...Algorithm import Algorithm
from ...DoubleImage import DoubleImage
from ...GlobalException import GlobalException
from ...Image import Image


class LoadFLO(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: Image | None = None
        self.path: str | None = None
        self.inputFields = "path"
        self.outputFields = "output"

    def execute(self) -> None:
        data = self._read(self.path)
        if self._bytes2Float(data, 0) != 202021.25:
            raise GlobalException("Sanity check failed")

        width = jint(jfloat(self._bytes2Integer(data, 4)))
        height = jint(jfloat(self._bytes2Integer(data, 8)))
        if width == 0 or height == 0:
            raise GlobalException("Invalid image size")

        self.output = DoubleImage(width, height, 1)
        self.output.fill(0.0)

        x = 0
        y = 0
        i = 12
        while i < len(data):
            u = self._bytes2Float(data, i)
            v = self._bytes2Float(data, i + 4)

            self.output.setXYDouble(
                x, y, (jatan2(v, u) + math.pi) / (2.0 * math.pi)
            )

            x = (x + 1) % width
            if x % width == 0:
                y += 1

            i += 8

    @staticmethod
    def _bytes2Float(data: bytes, index: int) -> float:
        return struct.unpack_from("<f", data, index)[0]

    @staticmethod
    def _bytes2Integer(data: bytes, index: int) -> int:
        return struct.unpack_from("<i", data, index)[0]

    @staticmethod
    def _read(path: str) -> bytes:
        try:
            with open(path, "rb") as f:
                return f.read()
        except FileNotFoundError:
            print("File not found")
        except OSError:
            traceback.print_exc()
        return b""

    @staticmethod
    def invoke(path: str) -> Image | None:
        try:
            return LoadFLO().preprocess(path)
        except GlobalException:
            traceback.print_exc()
        return None
