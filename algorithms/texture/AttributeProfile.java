/*  1:   */ package vpt.algorithms.texture;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.mm.gray.connected.GMaxTreeNonIncrAttrClosing;
/*  7:   */ import vpt.algorithms.mm.gray.connected.GMaxTreeNonIncrAttrOpening;
/*  8:   */ import vpt.algorithms.mm.gray.connected.attribute.Attribute;
/*  9:   */ import vpt.algorithms.statistical.Variance;
/* 10:   */ import vpt.util.Tools;
/* 11:   */ 
/* 12:   */ public class AttributeProfile
/* 13:   */   extends Algorithm
/* 14:   */ {
/* 15:24 */   public double[] output = null;
/* 16:26 */   public Image input = null;
/* 17:27 */   public Attribute[] criteria = null;
/* 18:28 */   public Integer valuation = null;
/* 19:   */   public static final int VOLUME = 0;
/* 20:   */   public static final int MOMENT1 = 1;
/* 21:   */   public static final int VARIANCE = 2;
/* 22:   */   
/* 23:   */   public AttributeProfile()
/* 24:   */   {
/* 25:35 */     this.inputFields = "input,criteria,valuation";
/* 26:36 */     this.outputFields = "output";
/* 27:   */   }
/* 28:   */   
/* 29:   */   public void execute()
/* 30:   */     throws GlobalException
/* 31:   */   {
/* 32:40 */     int cdim = this.input.getCDim();
/* 33:41 */     int size = this.criteria.length * cdim * 2;
/* 34:42 */     this.output = new double[size];
/* 35:   */     
/* 36:44 */     double[] originalValues = new double[cdim];
/* 37:46 */     if (this.valuation == null) {
/* 38:46 */       this.valuation = Integer.valueOf(0);
/* 39:   */     }
/* 40:48 */     for (int c = 0; c < cdim; c++) {
/* 41:49 */       originalValues[c] = valuation(this.input, c);
/* 42:   */     }
/* 43:51 */     for (int i = 0; i < this.criteria.length; i++)
/* 44:   */     {
/* 45:52 */       for (int c = 0; c < cdim; c++)
/* 46:   */       {
/* 47:53 */         Image tmp = GMaxTreeNonIncrAttrOpening.invoke(this.input.getChannel(c), this.criteria[i]);
/* 48:54 */         this.output[(c * this.criteria.length * 2 + i)] = (valuation(tmp, 0) / originalValues[c]);
/* 49:   */       }
/* 50:57 */       for (int c = 0; c < cdim; c++)
/* 51:   */       {
/* 52:58 */         Image tmp = GMaxTreeNonIncrAttrClosing.invoke(this.input.getChannel(c), this.criteria[i]);
/* 53:59 */         this.output[(c * this.criteria.length * 2 + this.criteria.length + i)] = (valuation(tmp, 0) / originalValues[c]);
/* 54:   */       }
/* 55:   */     }
/* 56:   */   }
/* 57:   */   
/* 58:   */   private double valuation(Image img, int channel)
/* 59:   */     throws GlobalException
/* 60:   */   {
/* 61:65 */     switch (this.valuation.intValue())
/* 62:   */     {
/* 63:   */     case 0: 
/* 64:67 */       return Tools.volume(img, channel);
/* 65:   */     case 2: 
/* 66:70 */       return Variance.invoke(img).doubleValue();
/* 67:   */     case 1: 
/* 68:73 */       return vpt.algorithms.statistical.InvariantMoment.invoke(img, Integer.valueOf(1))[0];
/* 69:   */     }
/* 70:76 */     throw new GlobalException("Invalid valuation type");
/* 71:   */   }
/* 72:   */   
/* 73:   */   public static double[] invoke(Image image, Attribute[] criteria, Integer valuation)
/* 74:   */   {
/* 75:   */     try
/* 76:   */     {
/* 77:83 */       return (double[])new AttributeProfile().preprocess(new Object[] { image, criteria, valuation });
/* 78:   */     }
/* 79:   */     catch (GlobalException e)
/* 80:   */     {
/* 81:85 */       e.printStackTrace();
/* 82:   */     }
/* 83:86 */     return null;
/* 84:   */   }
/* 85:   */   
/* 86:   */   public static double[] invoke(Image image, Attribute[] criteria)
/* 87:   */   {
/* 88:   */     try
/* 89:   */     {
/* 90:92 */       return (double[])new AttributeProfile().preprocess(new Object[] { image, criteria, null });
/* 91:   */     }
/* 92:   */     catch (GlobalException e)
/* 93:   */     {
/* 94:94 */       e.printStackTrace();
/* 95:   */     }
/* 96:95 */     return null;
/* 97:   */   }
/* 98:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.AttributeProfile
 * JD-Core Version:    0.7.0.1
 */