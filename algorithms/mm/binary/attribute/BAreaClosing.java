/*  1:   */ package vpt.algorithms.mm.binary.attribute;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ import java.util.Stack;
/*  6:   */ import vpt.Algorithm;
/*  7:   */ import vpt.BooleanImage;
/*  8:   */ import vpt.GlobalException;
/*  9:   */ import vpt.Image;
/* 10:   */ 
/* 11:   */ public class BAreaClosing
/* 12:   */   extends Algorithm
/* 13:   */ {
/* 14:23 */   public Image input = null;
/* 15:24 */   public Integer area = null;
/* 16:25 */   public Image output = null;
/* 17:   */   private BooleanImage mask;
/* 18:   */   private int xdim;
/* 19:   */   private int ydim;
/* 20:   */   
/* 21:   */   public BAreaClosing()
/* 22:   */   {
/* 23:32 */     this.inputFields = "input, area";
/* 24:33 */     this.outputFields = "output";
/* 25:   */   }
/* 26:   */   
/* 27:   */   private void filter(Stack<Point> s)
/* 28:   */   {
/* 29:38 */     ArrayList<Point> vp = new ArrayList();
/* 30:39 */     int size = 0;
/* 31:41 */     while (!s.isEmpty())
/* 32:   */     {
/* 33:42 */       Point p = (Point)s.pop();
/* 34:43 */       if ((p.x >= 0) && (p.x < this.xdim) && (p.y >= 0) && (p.y < this.ydim) && (!this.mask.getXYBoolean(p.x, p.y)))
/* 35:   */       {
/* 36:44 */         this.mask.setXYBoolean(p.x, p.y, true);
/* 37:45 */         vp.add(p);
/* 38:46 */         size++;
/* 39:47 */         s.push(new Point(p.x + 1, p.y - 1));
/* 40:48 */         s.push(new Point(p.x + 1, p.y));
/* 41:49 */         s.push(new Point(p.x + 1, p.y + 1));
/* 42:50 */         s.push(new Point(p.x - 1, p.y + 1));
/* 43:51 */         s.push(new Point(p.x - 1, p.y));
/* 44:52 */         s.push(new Point(p.x - 1, p.y - 1));
/* 45:53 */         s.push(new Point(p.x, p.y - 1));
/* 46:54 */         s.push(new Point(p.x, p.y + 1));
/* 47:   */       }
/* 48:   */     }
/* 49:58 */     if (size < this.area.intValue()) {
/* 50:59 */       for (Point p : vp) {
/* 51:60 */         this.output.setXYBoolean(p.x, p.y, true);
/* 52:   */       }
/* 53:   */     }
/* 54:   */   }
/* 55:   */   
/* 56:   */   public void execute()
/* 57:   */     throws GlobalException
/* 58:   */   {
/* 59:64 */     this.output = this.input.newInstance(true);
/* 60:65 */     this.mask = new BooleanImage(this.input, true);
/* 61:   */     
/* 62:67 */     this.xdim = this.input.getXDim();
/* 63:68 */     this.ydim = this.input.getYDim();
/* 64:   */     
/* 65:70 */     Stack<Point> s = new Stack();
/* 66:72 */     for (int x = 0; x < this.xdim; x++) {
/* 67:73 */       for (int y = 0; y < this.ydim; y++) {
/* 68:74 */         if (!this.mask.getXYBoolean(x, y))
/* 69:   */         {
/* 70:75 */           s.removeAllElements();
/* 71:76 */           s.push(new Point(x, y));
/* 72:77 */           filter(s);
/* 73:   */         }
/* 74:   */       }
/* 75:   */     }
/* 76:   */   }
/* 77:   */   
/* 78:   */   public static Image invoke(Image input, Integer area)
/* 79:   */   {
/* 80:   */     try
/* 81:   */     {
/* 82:86 */       return (Image)new BAreaClosing().preprocess(new Object[] { input, area });
/* 83:   */     }
/* 84:   */     catch (GlobalException e)
/* 85:   */     {
/* 86:88 */       e.printStackTrace();
/* 87:   */     }
/* 88:89 */     return null;
/* 89:   */   }
/* 90:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.binary.attribute.BAreaClosing
 * JD-Core Version:    0.7.0.1
 */