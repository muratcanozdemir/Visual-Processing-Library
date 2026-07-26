"""Port of ``vpt.GlobalException``."""

from __future__ import annotations


class GlobalException(Exception):
    """The library's single checked exception type."""

    def __init__(self, msg: str = None):
        super().__init__(msg) if msg is not None else super().__init__()
        self.msg = msg

    def getMessage(self) -> str:
        return self.msg
