"""Runs the README's Quick start snippet, so the documented example can't
silently go stale."""

from __future__ import annotations

import contextlib
import io
import re
from pathlib import Path

README = Path(__file__).parent.parent / "README.md"


def test_readme_quick_start_runs_and_prints_documented_output():
    text = README.read_text()
    section = text.split("## Quick start", 1)[1]
    code = re.search(r"```python\n(.*?)```", section, re.S).group(1)
    out = io.StringIO()
    with contextlib.redirect_stdout(out):
        exec(compile(code, "README.md", "exec"), {})
    assert out.getvalue().strip() == "True False"
