"""Port of ``vpt.Algorithm``.

Every algorithm in the library is a subclass. The Java version binds
positional arguments to named instance fields by reflection, driven by the
comma-separated ``inputFields`` / ``outputFields`` strings each subclass sets
in its constructor; ``preprocess`` then runs ``execute`` and reads the outputs
back out. Python's ``setattr``/``getattr`` express the same thing directly, so
the mechanism ports without a reflection shim.

One deliberate divergence: Java uses ``getDeclaredField``, which throws for an
unknown name, prints a stack trace, and then fails with a NullPointerException
on the following line. Here an unknown input name raises GlobalException
immediately, which surfaces the same mistake without the two-stage failure.
"""

from __future__ import annotations

from abc import ABC, abstractmethod

from ._javacompat import jsplit
from .GlobalException import GlobalException


class Algorithm(ABC):
    """Abstract base class for all algorithms."""

    def __init__(self):
        self.inputFields = ""
        self.outputFields = ""
        self._output = None

    # ------------------------------------------------------------------
    @staticmethod
    def _getFields(s: str) -> list[str]:
        # Java's String.split(",") (no limit) drops trailing empty strings,
        # e.g. hough.HoughTransformLine's inputFields = "input," (trailing
        # comma) splits to a single-element ["input"] in Java. Python's
        # str.split doesn't drop them, which previously made this method
        # over-count the field list for any inputFields/outputFields string
        # ending in a comma -- a real Python-port bug, not a Java behavior
        # to preserve, since it made an otherwise-correct algorithm's
        # invoke() always fail with a bogus "Requires N parameters!".
        return [t.strip() for t in jsplit(s, ",")]

    def _setInputFields(self, params) -> None:
        inputFieldNames = self._getFields(self.inputFields)

        if len(inputFieldNames) != len(params):
            raise GlobalException(f"Requires {len(inputFieldNames)} parameters!")

        for name, value in zip(inputFieldNames, params):
            if not hasattr(self, name):
                raise GlobalException(
                    f"{type(self).__name__} declares input field '{name}' "
                    f"but has no such attribute"
                )
            setattr(self, name, value)

    def _getOutputFields(self) -> None:
        outputFieldNames = self._getFields(self.outputFields)

        if not outputFieldNames or len(outputFieldNames[0]) == 0:
            return

        self._output = [getattr(self, name) for name in outputFieldNames]

    # ------------------------------------------------------------------
    def preprocess(self, *params):
        """Bind inputs, execute, and return the first declared output."""
        self._setInputFields(params)
        self.execute()
        self._getOutputFields()

        if not self._output:
            return None
        return self._output[0]

    def preprocessAll(self, *params):
        """Bind inputs, execute, and return the full list of outputs."""
        self._setInputFields(params)
        self.execute()
        self._getOutputFields()

        if not self._output:
            return None
        return self._output

    @abstractmethod
    def execute(self) -> None:
        """Run the algorithm, reading input fields and writing output fields."""
