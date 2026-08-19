"""Port of the ``vpt.util.ordering`` package.

Vector orderings for multivariate morphology. There is no canonical total
order on colour vectors, so the library supplies a family of strategies
behind the Ordering interface.

Not yet ported (Tier 0 remainder): AlphaTrimmedLexico, EndMember,
EndMemberExtrema, MarginalEndMember, QuantaLexHLS, QuantaLexLAB,
QuantaLexLS, QuantaLexLSH.
"""

from .AlgebraicOrdering import AlgebraicOrdering
from .BitMix import BitMix
from .CompositeOrdering import CompositeOrdering
from .CustomLexico import CustomLexico
from .EuclideanNorm import EuclideanNorm
from .Lexico import Lexico
from .LexicoCIELAB import LexicoCIELAB
from .Ordering import Ordering
from .ReverseLexico import ReverseLexico
from .SpectralAngular import SpectralAngular

__all__ = [
    "AlgebraicOrdering",
    "BitMix",
    "CompositeOrdering",
    "CustomLexico",
    "EuclideanNorm",
    "Lexico",
    "LexicoCIELAB",
    "Ordering",
    "ReverseLexico",
    "SpectralAngular",
]
