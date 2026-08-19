"""Port of ``vpt.algorithms.mm.multi.connected.maxtree.MyListIterator``.

Dead code: the Java class exists and implements ``Iterator<T>``, but nothing
in the codebase ever constructs one (every traversal in this package walks
``MyList`` manually via ``getHead()``/``.next`` instead) and its constructor
isn't even public. Ported for API parity only.
"""

from __future__ import annotations

import sys

from .MyList import MyList
from .MyListNode import MyListNode


class MyListIterator:
    def __init__(self, myList: MyList):
        self.mylist = myList
        self.current: MyListNode | None = myList.getHead()

    def hasNext(self) -> bool:
        return self.current is not None

    def next(self):
        if self.hasNext():
            result = self.current.getDatum()
            self.current = self.current.next
            return result
        return None

    def remove(self) -> None:
        print("MyListIterator does not support removal, yet.", file=sys.stderr)
