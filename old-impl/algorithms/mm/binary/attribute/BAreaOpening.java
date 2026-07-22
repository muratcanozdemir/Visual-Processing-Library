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
/* 11:   */ public class BAreaOpening
/* 12:   */   extends Algorithm
/* 13:   */ {
/* 14:24 */   public Image input = null;
/* 15:25 */   public Integer area = null;
/* 16:26 */   public Image output = null;
/* 17:   */   private BooleanImage mask;
/* 18:   */   private int xdim;
/* 19:   */   private int ydim;
/* 20:   */   
/* 21:   */   public BAreaOpening()
/* 22:   */   {
/* 23:33 */     this.inputFields = "input, area";
/* 24:34 */     this.outputFields = "output";
/* 25:   */   }
/* 26:   */   
/* 27:   */   private void filter(Stack<Point> s)
/* 28:   */   {
/* 29:39 */     ArrayList<Point> vp = new ArrayList();
/* 30:40 */     int size = 0;
/* 31:42 */     while (!s.isEmpty())
/* 32:   */     {
/* 33:43 */       Point p = (Point)s.pop();
/* 34:45 */       if ((p.x >= 0) && (p.x < this.xdim) && (p.y >= 0) && (p.y < this.ydim) && (this.mask.getXYBoolean(p.x, p.y)))
/* 35:   */       {
/* 36:46 */         this.mask.setXYBoolean(p.x, p.y, false);
/* 37:47 */         vp.add(p);
/* 38:48 */         size++;
/* 39:49 */         s.push(new Point(p.x + 1, p.y - 1));
/* 40:50 */         s.push(new Point(p.x + 1, p.y));
/* 41:51 */         s.push(new Point(p.x + 1, p.y + 1));
/* 42:52 */         s.push(new Point(p.x - 1, p.y + 1));
/* 43:53 */         s.push(new Point(p.x - 1, p.y));
/* 44:54 */         s.push(new Point(p.x - 1, p.y - 1));
/* 45:55 */         s.push(new Point(p.x, p.y - 1));
/* 46:56 */         s.push(new Point(p.x, p.y + 1));
/* 47:   */       }
/* 48:   */     }
/* 49:60 */     if (size < this.area.intValue()) {
/* 50:61 */       for (Point p : vp) {
/* 51:62 */         this.output.setXYBoolean(p.x, p.y, false);
/* 52:   */       }
/* 53:   */     }
/* 54:   */   }
/* 55:   */   
/* 56:   */   public void execute()
/* 57:   */     throws GlobalException
/* 58:   */   {
/* 59:67 */     this.output = this.input.newInstance(true);
/* 60:68 */     this.mask = new BooleanImage(this.input, true);
/* 61:   */     
/* 62:70 */     this.xdim = this.input.getXDim();
/* 63:71 */     this.ydim = this.input.getYDim();
/* 64:   */     
/* 65:73 */     Stack<Point> s = new Stack();
/* 66:75 */     for (int x = 0; x < this.xdim; x++) {
/* 67:76 */       for (int y = 0; y < this.ydim; y++) {
/* 68:77 */         if (this.mask.getXYBoolean(x, y))
/* 69:   */         {
/* 70:78 */           s.removeAllElements();
/* 71:79 */           s.push(new Point(x, y));
/* 72:80 */           filter(s);
/* 73:   */         }
/* 74:   */       }
/* 75:   */     }
/* 76:   */   }
/* 77:   */   
/* 78:   */   public static Image invoke(Image input, Integer area)
/* 79:   */   {
/* 80:   */     try
/* 81:   */     {
/* 82:88 */       return (Image)new BAreaOpening().preprocess(new Object[] { input, area });
/* 83:   */     }
/* 84:   */     catch (GlobalException e)
/* 85:   */     {
/* 86:90 */       e.printStackTrace();
/* 87:   */     }
/* 88:91 */     return null;
/* 89:   */   }
/* 90:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.binary.attribute.BAreaOpening
 * JD-Core Version:    0.7.0.1
 */