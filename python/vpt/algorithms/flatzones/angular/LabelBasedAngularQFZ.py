"""Port of ``vpt.algorithms.flatzones.angular.LabelBasedAngularQFZ``: a
thin wrapper that converts a hue channel to a per-pixel byte distance from
a reference hue (``conversion.Hue2Distance``) and runs the plain
byte-difference ``flatzones.gray.GrayQFZ`` on that, rather than doing
circular quasi-flat-zone growth directly on the hue channel the way
``AngularQFZ``/``AngularQFZ2``/``AngularQFZ5``/``AngularQFZ6`` do."""

from __future__ import annotations

import traceback

from ....Algorithm import Algorithm
from ....GlobalException import GlobalException
from ....Image import Image
from ...conversion.Hue2Distance import Hue2Distance
from ..gray.GrayQFZ import GrayQFZ


class LabelBasedAngularQFZ(Algorithm):
    def __init__(self):
        super().__init__()
        self.input: Image | None = None
        self.output: Image | None = None
        self.alpha: int | None = None
        self.omega: int | None = None
        self.refHue: float | None = None
        self.inputFields = "input,alpha,omega,refHue"
        self.outputFields = "output"

    def execute(self) -> None:
        if self.input.getCDim() != 1:
            raise GlobalException(
                "This implementation is only for monochannel angular data images."
            )
        if self.alpha <= 0 or self.alpha >= 255 or self.omega <= 0 or self.omega >= 255:
            raise GlobalException("Arguments out of range.")

        dist = Hue2Distance.invoke(self.input.getChannel(0), self.refHue)
        self.output = GrayQFZ.invoke(dist, self.alpha, self.omega)

    @staticmethod
    def invoke(img: Image, alpha: int, omega: int, refHue: float) -> Image | None:
        try:
            return LabelBasedAngularQFZ().preprocess(img, alpha, omega, refHue)
        except GlobalException:
            traceback.print_exc()
        return None
