package vpt.algorithms.mm.multi.connected.attribute;

import java.awt.Point;
import vpt.Image;
import vpt.algorithms.mm.multi.connected.maxtree.MyList;

public abstract interface MultiAttribute
{
  public abstract boolean test(MyList<Point> paramMyList, Image paramImage);
}


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.connected.attribute.MultiAttribute
 * JD-Core Version:    0.7.0.1
 */