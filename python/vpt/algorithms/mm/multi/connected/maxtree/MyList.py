"""Port of ``vpt.algorithms.mm.multi.connected.maxtree.MyList``: a bespoke
doubly-linked list, used throughout ``mm/multi/connected`` in place of
``ArrayList`` (unlike the scalar ``mm/gray/connected`` max-tree family,
which does use ``ArrayList``/plain Python ``list``). Kept as a real linked
structure here rather than collapsed to a Python ``list``, because its
exact node-level behaviour is depended on:

- ``remove(t)`` unlinks ``t`` by rewiring its *neighbours'* ``next``/
  ``previous`` pointers, but never clears ``t``'s own ``next``/``previous``.
  So a ``MyListIterator`` (or the manual ``getHead()``/``.next`` walks this
  package uses instead) that is currently sitting on the just-removed node
  keeps working: it reads the removed node's still-valid ``.next`` and
  continues forward through the list exactly as if nothing had happened.
  This is the mechanism that lets ``MMaxTreeIncrAttrOpening``/
  ``MMaxTreeNonIncrAttrOpening`` call ``n.parent.children.remove(n)`` safely
  -- Python's ``list.remove`` has no equivalent guarantee, so a plain list
  would only coincidentally behave the same (and does, in this port,
  because every place that mutates a children/pixels list finishes any
  traversal of the *same* list before mutating it -- see the ``MMaxTree*``
  docstrings). Ported as a real linked list anyway, on the theory that
  matching the data structure is safer than matching today's call sites.
- Search is by ``==`` (Java ``Object.equals``, unoverridden on
  ``MaxTreeNode`` so it's identity; overridden on ``Point`` as value
  equality) -- Python's default ``==``/``is`` line up the same way given
  ``_javacompat.Point`` already defines value ``__eq__``.

PRESERVED BUG: ``addAll`` never checks whether *this* list (as opposed to
the argument) is empty before dereferencing ``self.tail.next`` -- calling
``emptyList.addAll(nonEmptyList)`` throws a Java NullPointerException, and
raises the Python equivalent (``AttributeError`` on ``None.next``) here.
Every actual call site in this package (``MultivariateMaxTree``'s
``q.pixels.addAll(p.pixels)``/``q.children.addAll(p.children)``) only
calls this on a node that was seeded with at least one pixel during
flooding, so it's never hit in practice -- not guarded against here either,
to match.

``main`` (a scratch ``public static void main`` demo, unrelated to any
production code path) is not ported, matching this project's treatment of
other dead demo/GUI entry points.
"""

from __future__ import annotations

from ......_javacompat import Point
from .MyListNode import MyListNode


class MyList:
    def __init__(self):
        self.head: MyListNode | None = None
        self.tail: MyListNode | None = None

    def getHead(self) -> "MyListNode | None":
        return self.head

    def getTail(self) -> "MyListNode | None":
        return self.tail

    def toArray(self) -> list[Point]:
        n = self.head
        array = []
        while n is not None:
            array.append(n.datum)
            n = n.next
        return array

    def addFirst(self, datum) -> None:
        if datum is None:
            return
        ln = MyListNode(datum)
        if self.head is None and self.tail is None:
            self.head = ln
            self.tail = ln
            ln.next = None
            ln.previous = None
        else:
            ln.next = self.head
            ln.previous = None
            self.head.previous = ln

            self.head = ln

    def addLast(self, datum) -> None:
        if datum is None:
            return
        ln = MyListNode(datum)
        if self.head is None and self.tail is None:
            self.head = ln
            self.tail = ln
            ln.next = None
            ln.previous = None
        else:
            self.tail.next = ln
            ln.previous = self.tail
            ln.next = None
            self.tail = ln

    def addAll(self, other: "MyList | None") -> None:
        if other is None:
            return
        if other.head is None or other.tail is None:
            return
        self.tail.next = other.head
        other.head.previous = self.tail
        self.tail = other.tail

    def remove(self, t) -> None:
        n = None
        tmp = self.head
        while tmp is not None:
            if tmp.datum == t:
                n = tmp
                if n.previous is not None:
                    n.previous.next = n.next
                else:
                    self.head = n.next
                if n.next is not None:
                    n.next.previous = n.previous
                    break
                self.tail = n.previous
                break
            tmp = tmp.next

    def size(self) -> int:
        size = 0
        tmp = self.head
        while tmp is not None:
            size += 1
            tmp = tmp.next
        return size
