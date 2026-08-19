"""Port of the ``vpt.algorithms.shape`` package -- complete (32/32 in-scope
files).

**Confirmed to hang forever** (same already-documented ``BFillHole``/
``ConvexHull(filled=True)`` mechanism, confirmed against the JDK 21 oracle
this session -- see ``ConvexAreaRatio.py``'s docstring): ``ConvexAreaRatio``,
``ConvexPerimeterRatio``, ``Dispersion``, ``EdgeBackForeRatioHistogram``.
Ported faithfully but not smoke-tested or golden-tested, for the same reason
``BFillHole`` isn't.

**Confirmed to always return ``None``** (verified via ``javap`` bytecode
disassembly, not just source reading): ``MultiLobeMeanShapeFeatures`` (a
genuine no-op stub -- ``execute()`` never assigns ``output``),
``AxisOfSymmetry`` (same -- the single ``putfield`` to "output" in the
bytecode is the constructor's *string* field, not the image field),
``CentroidContourDistance`` (``execute()`` never assigns ``output`` *and*
``invoke`` calls ``preprocess`` with 2 arguments against a 1-name
``inputFields``, so ``Algorithm``'s own parameter-count check raises before
``execute()`` ever runs).

``FPFT`` and ``DFTZernikeMoments`` inherit the ``FFT``/``IFFT``
always-returns-all-zero bug (``ComplexNumber.cSum``/``cDiff`` mutate their
first argument in place and return a fresh all-zero array -- see
``vpt/algorithms/frequential/``), so both always produce a degenerate
(all-zero or constant) result.

``WidthFactor`` (and its duplicates ``AreaWidthFactor``,
``NormalizedWidthFactor``) preserve a real ``javap``-confirmed int-truncation
bug: the numerator/denominator of the ratio are computed via ``int``
subtraction before being cast to ``double``, not via double subtraction.

Duplicate-class pairs (byte-for-byte identical Java, ported as independent
files rather than refactored into shared code, to keep 1:1 diffability with
the Java source): ``AreaWidthFactor``/``NormalizedWidthFactor``,
``RegionalMomentsOfInertia``/``RegionalMomentsOfInertia2``,
``GrayConnectedComponents``/``GrayConnectedComponents2`` (the latter differs
only in reporting components as a per-pixel ``[x][y]`` lookup table instead
of a flat list).

``BorderCovariance`` and ``BorderGranulometry`` are out of scope (depend
on the unported `texture` package); ``mpeg7shapeB.BullsEyeScore`` (fails
to decompile at all) and ``mpeg7shapeB.FeatureExtractor`` (a dataset-path
demo script, no reusable API) are out of scope too."""

from .AngleCodeHistogram import AngleCodeHistogram
from .AreaWidthFactor import AreaWidthFactor
from .AxisOfSymmetry import AxisOfSymmetry
from .BasicShapeStatistics import BasicShapeStatistics
from .CentroidContourDistance import CentroidContourDistance
from .CircularWidthFactor import CircularWidthFactor
from .CircularWidthFactor2 import CircularWidthFactor2
from .CircularWidthFactor3 import CircularWidthFactor3
from .Compactness import Compactness
from .ConnectedComponentFromSeed import ConnectedComponentFromSeed
from .ConnectedComponents import ConnectedComponents
from .ContourPointDistributionHistogram import ContourPointDistributionHistogram
from .ConvexAreaRatio import ConvexAreaRatio
from .ConvexPerimeterRatio import ConvexPerimeterRatio
from .DFTZernikeMoments import DFTZernikeMoments
from .Dispersion import Dispersion
from .DominantPointsDetection import DominantPointsDetection
from .EdgeBackForeRatioHistogram import EdgeBackForeRatioHistogram
from .FourierDescriptor import FourierDescriptor
from .FPFT import FPFT
from .GrayConnectedComponents import GrayConnectedComponents
from .GrayConnectedComponents2 import GrayConnectedComponents2
from .MeanDistanceToCentroid import MeanDistanceToCentroid
from .MorphologicalElongation import MorphologicalElongation
from .MultiLobeMeanShapeFeatures import MultiLobeMeanShapeFeatures
from .MultiScaleCCCount import MultiScaleCCCount
from .NormalizedWidthFactor import NormalizedWidthFactor
from .Orientation import Orientation
from .RegionalMomentsOfInertia import RegionalMomentsOfInertia
from .RegionalMomentsOfInertia2 import RegionalMomentsOfInertia2
from .VarianceOfDistanceToCentroid import VarianceOfDistanceToCentroid
from .WidthFactor import WidthFactor
from .ZernikeMoments import ZernikeMoments

__all__ = [
    "AngleCodeHistogram",
    "AreaWidthFactor",
    "AxisOfSymmetry",
    "BasicShapeStatistics",
    "CentroidContourDistance",
    "CircularWidthFactor",
    "CircularWidthFactor2",
    "CircularWidthFactor3",
    "Compactness",
    "ConnectedComponentFromSeed",
    "ConnectedComponents",
    "ContourPointDistributionHistogram",
    "ConvexAreaRatio",
    "ConvexPerimeterRatio",
    "DFTZernikeMoments",
    "Dispersion",
    "DominantPointsDetection",
    "EdgeBackForeRatioHistogram",
    "FPFT",
    "FourierDescriptor",
    "GrayConnectedComponents",
    "GrayConnectedComponents2",
    "MeanDistanceToCentroid",
    "MorphologicalElongation",
    "MultiLobeMeanShapeFeatures",
    "MultiScaleCCCount",
    "NormalizedWidthFactor",
    "Orientation",
    "RegionalMomentsOfInertia",
    "RegionalMomentsOfInertia2",
    "VarianceOfDistanceToCentroid",
    "WidthFactor",
    "ZernikeMoments",
]
