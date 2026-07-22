/*  1:   */ package vpt.algorithms.texture;
/*  2:   */ 
/*  3:   */ import java.util.ArrayList;
/*  4:   */ import java.util.Arrays;
/*  5:   */ import vpt.Algorithm;
/*  6:   */ import vpt.GlobalException;
/*  7:   */ import vpt.Image;
/*  8:   */ import vpt.algorithms.mm.gray.connected.maxtree.MaxTree_yedek;
/*  9:   */ import vpt.algorithms.mm.gray.connected.maxtree.Node_yedek;
/* 10:   */ 
/* 11:   */ public class CCBasedAreaProfile
/* 12:   */   extends Algorithm
/* 13:   */ {
/* 14:24 */   public Image input = null;
/* 15:25 */   public Integer size = null;
/* 16:26 */   public double[] output = null;
/* 17:   */   private int numberOfNodes;
/* 18:   */   
/* 19:   */   public CCBasedAreaProfile()
/* 20:   */   {
/* 21:31 */     this.inputFields = "input, size";
/* 22:32 */     this.outputFields = "output";
/* 23:   */   }
/* 24:   */   
/* 25:   */   public void execute()
/* 26:   */     throws GlobalException
/* 27:   */   {
/* 28:37 */     if (this.input.getCDim() != 1) {
/* 29:37 */       throw new GlobalException("Only Grayscale Images for now...");
/* 30:   */     }
/* 31:40 */     this.output = new double[this.size.intValue()];
/* 32:41 */     Arrays.fill(this.output, 0.0D);
/* 33:   */     
/* 34:43 */     this.numberOfNodes = 0;
/* 35:   */     
/* 36:   */ 
/* 37:46 */     Node_yedek root = MaxTree_yedek.invoke(this.input);
/* 38:   */     
/* 39:   */ 
/* 40:49 */     processNode(root);
/* 41:52 */     for (int i = 0; i < this.output.length; i++) {
/* 42:53 */       this.output[i] /= this.numberOfNodes;
/* 43:   */     }
/* 44:   */   }
/* 45:   */   
/* 46:   */   private void processNode(Node_yedek node)
/* 47:   */   {
/* 48:58 */     double area = node.pixels.size();
/* 49:59 */     double ratio = 1.0D;
/* 50:61 */     if (node.parent != null) {
/* 51:62 */       ratio = area / node.parent.pixels.size();
/* 52:   */     }
/* 53:65 */     int index = (int)Math.ceil(ratio * this.size.intValue());
/* 54:66 */     if (index > this.size.intValue()) {
/* 55:66 */       index = this.size.intValue();
/* 56:   */     }
/* 57:67 */     if (index < 1) {
/* 58:67 */       index = 1;
/* 59:   */     }
/* 60:68 */     this.output[(index - 1)] += 1.0D;
/* 61:69 */     this.numberOfNodes += 1;
/* 62:71 */     for (Node_yedek n : node.children) {
/* 63:72 */       processNode(n);
/* 64:   */     }
/* 65:   */   }
/* 66:   */   
/* 67:   */   public static double[] invoke(Image image, Integer size)
/* 68:   */   {
/* 69:   */     try
/* 70:   */     {
/* 71:77 */       return (double[])new CCBasedAreaProfile().preprocess(new Object[] { image, size });
/* 72:   */     }
/* 73:   */     catch (GlobalException e)
/* 74:   */     {
/* 75:79 */       e.printStackTrace();
/* 76:   */     }
/* 77:80 */     return null;
/* 78:   */   }
/* 79:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.CCBasedAreaProfile
 * JD-Core Version:    0.7.0.1
 */