"""Port of ``vpt.algorithms.mm.multi.connected.maxtree.MyListNode``: a node
in the bespoke doubly-linked list ``MyList`` uses instead of ``ArrayList``.

``Equals`` (capital E -- not an ``Object.equals`` override, a distinct
method the Java never actually calls anywhere) is transcribed for API
parity but is dead code, same as in the Java.
"""

from __future__ import annotations

_counter = 0


class MyListNode:
    __slots__ = ("datum", "next", "previous", "serial")

    def __init__(self, datum):
        global _counter
        self.datum = datum
        self.next: MyListNode | None = None
        self.previous: MyListNode | None = None
        self.serial = _counter
        _counter += 1

    def Equals(self, o: "MyListNode") -> bool:
        return o.serial == self.serial

    def getDatum(self):
        return self.datum

    def getNext(self) -> "MyListNode | None":
        return self.next
