"""Port of ``vpt.algorithms.shape.MultiLobeMeanShapeFeatures``.

**Genuinely unfinished in the Java**: ``execute()`` only validates
``cdim == 1`` and never assigns ``output`` -- ``invoke`` therefore always
returns ``None``. Not a decompiler artefact (there is simply no more
bytecode in the method); the class appears to be a stub the original
author never finished. Transcribed as the same no-op. Its Java ``main``
(a hardcoded local dataset path, calling the unrelated
``MultiScaleCCCount``, not this class's own logic) is not ported."""

from __future__ import annotations

import traceback

from ...Algorithm import Algorithm
from ...GlobalException import GlobalException
from ...Image import Image


class MultiLobeMeanShapeFeatures(Algorithm):
    def __init__(self):
        super().__init__()
        self.output: list[float] | None = None
        self.input: Image | None = None
        self.inputFields = "input"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.input.getCDim() != 1:
            raise GlobalException("Sorry, only mono-channel images for now...")

    @staticmethod
    def invoke(img: Image) -> list[float] | None:
        try:
            return MultiLobeMeanShapeFeatures().preprocess(img)
        except GlobalException:
            traceback.print_exc()
        return None
