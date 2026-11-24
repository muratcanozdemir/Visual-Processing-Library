"""Guard: no transcendental function may bypass ``vpt._javacompat``.

The platform's math library (``math.*``, numpy's ufuncs, and Python's own
``**`` on floats, which calls C ``pow``) gives different last bits on
different OSes and CPUs -- the golden tests passed on Linux and failed on
macOS/Windows because of it. ``vpt._javacompat``'s ``j*`` helpers compute
through ``vpt._native`` instead, the same everywhere. This test parses
every module and fails on any direct use outside ``_javacompat``, so new
code can't quietly reintroduce platform math.

Allowed: ``sqrt`` (IEEE 754 requires it correctly rounded, so every
implementation agrees), and ``**`` where both operands are integer
literals or the exponent is the integer literal 2 on a known-integer
expression is *not* special-cased -- write ``x * x`` instead.
"""

from __future__ import annotations

import ast
from pathlib import Path

import vpt

ROOT = Path(vpt.__file__).parent
EXEMPT = {ROOT / "_javacompat.py"}

MATH_FUNCS = {
    "sin", "cos", "tan", "asin", "acos", "atan", "atan2", "sinh", "cosh", "tanh",
    "asinh", "acosh", "atanh", "exp", "exp2", "expm1", "log", "log2", "log10", "log1p",
    "pow", "hypot", "cbrt", "erf", "erfc", "gamma", "lgamma", "dist",
}
NUMPY_FUNCS = {
    "sin", "cos", "tan", "arcsin", "arccos", "arctan", "arctan2", "sinh", "cosh", "tanh",
    "arcsinh", "arccosh", "arctanh", "exp", "exp2", "expm1", "log", "log2", "log10",
    "log1p", "power", "float_power", "hypot", "cbrt", "angle", "logaddexp", "logaddexp2",
    "deg2rad", "rad2deg", "sinc",
}


def _is_int_literal(node: ast.AST) -> bool:
    if isinstance(node, ast.UnaryOp) and isinstance(node.op, ast.USub):
        node = node.operand
    return isinstance(node, ast.Constant) and type(node.value) is int


def violations(path: Path) -> list[str]:
    tree = ast.parse(path.read_text(), filename=str(path))
    found = []
    for node in ast.walk(tree):
        where = f"{path.relative_to(ROOT.parent)}:{getattr(node, 'lineno', '?')}"
        if isinstance(node, ast.Attribute) and isinstance(node.value, ast.Name):
            mod, attr = node.value.id, node.attr
            if mod == "math" and attr in MATH_FUNCS:
                found.append(f"{where}: math.{attr}")
            elif mod in ("np", "numpy") and attr in NUMPY_FUNCS:
                found.append(f"{where}: np.{attr}")
        elif isinstance(node, ast.BinOp) and isinstance(node.op, ast.Pow):
            if not (_is_int_literal(node.left) and _is_int_literal(node.right)):
                found.append(f"{where}: ** ({ast.unparse(node)})")
        elif isinstance(node, ast.AugAssign) and isinstance(node.op, ast.Pow):
            found.append(f"{where}: **=")
        elif isinstance(node, ast.Call) and isinstance(node.func, ast.Name):
            if node.func.id == "pow":
                found.append(f"{where}: pow()")
        elif isinstance(node, ast.ImportFrom) and node.module in ("math", "numpy"):
            names = {a.name for a in node.names}
            bad = names & (MATH_FUNCS | NUMPY_FUNCS)
            if bad:
                found.append(f"{where}: from {node.module} import {sorted(bad)}")
    return found


def test_no_platform_math_outside_javacompat():
    found = []
    for path in sorted(ROOT.rglob("*.py")):
        if path not in EXEMPT:
            found += violations(path)
    assert not found, (
        f"{len(found)} platform-math call(s); route them through vpt._javacompat's "
        "j* helpers:\n  " + "\n  ".join(found)
    )
