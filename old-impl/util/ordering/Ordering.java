package vpt.util.ordering;

public abstract interface Ordering
{
  public abstract double[] max(double[][] paramArrayOfDouble);
  
  public abstract double[] min(double[][] paramArrayOfDouble);
  
  public abstract double[] rank(double[][] paramArrayOfDouble, int paramInt);
}


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.ordering.Ordering
 * JD-Core Version:    0.7.0.1
 */