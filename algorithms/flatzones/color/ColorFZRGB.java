/*   1:    */ package vpt.algorithms.flatzones.color;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Stack;
/*   7:    */ import vpt.Algorithm;
/*   8:    */ import vpt.BooleanImage;
/*   9:    */ import vpt.GlobalException;
/*  10:    */ import vpt.Image;
/*  11:    */ import vpt.IntegerImage;
/*  12:    */ 
/*  13:    */ public class ColorFZRGB
/*  14:    */   extends Algorithm
/*  15:    */ {
/*  16:    */   public Image input;
/*  17:    */   public IntegerImage output;
/*  18: 27 */   private ArrayList<Point> list2 = new ArrayList();
/*  19: 28 */   private Stack<Point> s = new Stack();
/*  20:    */   private int xdim;
/*  21:    */   private int ydim;
/*  22:    */   BooleanImage temp;
/*  23: 40 */   private Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  24: 41 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  25:    */   
/*  26:    */   public ColorFZRGB()
/*  27:    */   {
/*  28: 44 */     this.inputFields = "input";
/*  29: 45 */     this.outputFields = "output";
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void execute()
/*  33:    */     throws GlobalException
/*  34:    */   {
/*  35: 50 */     this.xdim = this.input.getXDim();
/*  36: 51 */     this.ydim = this.input.getYDim();
/*  37: 53 */     if (this.input.getCDim() != 3) {
/*  38: 53 */       throw new GlobalException("This implementation is only for color images");
/*  39:    */     }
/*  40: 55 */     this.output = new IntegerImage(this.xdim, this.ydim, 1);
/*  41: 56 */     this.output.fill(0);
/*  42:    */     
/*  43: 58 */     this.temp = new BooleanImage(this.xdim, this.ydim, 1);
/*  44: 59 */     this.temp.fill(false);
/*  45:    */     
/*  46: 61 */     int label = 1;
/*  47: 63 */     for (int x = 0; x < this.xdim; x++) {
/*  48: 68 */       for (int y = 0; y < this.ydim; y++) {
/*  49: 71 */         if (this.output.getXYInt(x, y) <= 0)
/*  50:    */         {
/*  51: 73 */           Point t = new Point(x, y);
/*  52:    */           
/*  53: 75 */           createFZ(t, label++);
/*  54:    */         }
/*  55:    */       }
/*  56:    */     }
/*  57: 79 */     System.out.println("Total number of quasi flat zones: " + (label - 1));
/*  58:    */   }
/*  59:    */   
/*  60:    */   private void createFZ(Point r, int label)
/*  61:    */   {
/*  62: 90 */     this.s.clear();
/*  63: 91 */     this.s.add(r);
/*  64: 92 */     this.temp.setXYBoolean(r.x, r.y, true);
/*  65: 93 */     this.list2.add(r);
/*  66:    */     int x;
/*  67:    */     int i;
/*  68: 95 */     for (; !this.s.isEmpty(); i < this.N.length)
/*  69:    */     {
/*  70: 97 */       Point tmp = (Point)this.s.pop();
/*  71:    */       
/*  72: 99 */       x = tmp.x;
/*  73:100 */       int y = tmp.y;
/*  74:    */       
/*  75:102 */       int[] p = this.input.getVXYByte(x, y);
/*  76:103 */       this.output.setXYInt(x, y, label);
/*  77:    */       
/*  78:105 */       i = 0; continue;
/*  79:106 */       int _x = x + this.N[i].x;
/*  80:107 */       int _y = y + this.N[i].y;
/*  81:109 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim)) {
/*  82:111 */         if (!this.temp.getXYBoolean(_x, _y))
/*  83:    */         {
/*  84:113 */           int[] q = this.input.getVXYByte(_x, _y);
/*  85:115 */           if ((q[0] == p[0]) && (q[1] == p[1]) && (q[2] == p[2]))
/*  86:    */           {
/*  87:117 */             Point t = new Point(_x, _y);
/*  88:118 */             this.s.add(t);
/*  89:    */             
/*  90:120 */             this.temp.setXYBoolean(t.x, t.y, true);
/*  91:121 */             this.list2.add(t);
/*  92:    */           }
/*  93:    */         }
/*  94:    */       }
/*  95:105 */       i++;
/*  96:    */     }
/*  97:126 */     for (Point t : this.list2) {
/*  98:127 */       this.temp.setXYBoolean(t.x, t.y, false);
/*  99:    */     }
/* 100:128 */     this.list2.clear();
/* 101:    */   }
/* 102:    */   
/* 103:    */   public static Image invoke(Image img)
/* 104:    */   {
/* 105:    */     try
/* 106:    */     {
/* 107:133 */       return (IntegerImage)new ColorFZRGB().preprocess(new Object[] { img });
/* 108:    */     }
/* 109:    */     catch (GlobalException e)
/* 110:    */     {
/* 111:135 */       e.printStackTrace();
/* 112:    */     }
/* 113:136 */     return null;
/* 114:    */   }
/* 115:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.flatzones.color.ColorFZRGB
 * JD-Core Version:    0.7.0.1
 */