"""Port of ``vpt.util.ordering.Ordering``.

The interface every vector ordering implements. Multivariate morphology needs
a total order on colour/spectral vectors, and there is no canonical one, so
the library supplies a family of strategies behind this interface.
"""

from __future__ import annotations

from abc import ABC, abstractmethod


class Ordering(ABC):
    @abstractmethod
    def max(self, v):
        """Greatest of the vectors in `v` (a sequence of vectors)."""

    @abstractmethod
    def min(self, v):
        """Least of the vectors in `v`."""

    @abstractmethod
    def rank(self, v, r: int):
        """The r-th vector of `v` in ascending order."""
