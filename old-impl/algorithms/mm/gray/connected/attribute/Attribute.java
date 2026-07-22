package vpt.algorithms.mm.gray.connected.attribute;

import java.awt.Point;
import java.util.ArrayList;
import vpt.Image;

public abstract interface Attribute
{
  public abstract boolean test(ArrayList<Point> paramArrayList, Image paramImage);
}


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.attribute.Attribute
 * JD-Core Version:    0.7.0.1
 */