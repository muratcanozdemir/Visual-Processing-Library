/*   1:    */ package vpt.algorithms.mm.gray.connected.maxtree;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Arrays;
/*   7:    */ import vpt.Algorithm;
/*   8:    */ import vpt.ByteImage;
/*   9:    */ import vpt.GlobalException;
/*  10:    */ import vpt.Image;
/*  11:    */ import vpt.IntegerImage;
/*  12:    */ import vpt.util.HierarchicalQueue;
/*  13:    */ import vpt.util.Tools;
/*  14:    */ 
/*  15:    */ public class MaxTree_yedek
/*  16:    */   extends Algorithm
/*  17:    */ {
/*  18: 26 */   public Image input = null;
/*  19: 27 */   public Node_yedek root = null;
/*  20: 29 */   private HierarchicalQueue queue = null;
/*  21: 30 */   private Image statuses = null;
/*  22: 31 */   private int[] numberOfNodes = null;
/*  23: 33 */   private boolean[] nodeAtLevel = null;
/*  24: 35 */   private ArrayList<Node_yedek>[] nodes = null;
/*  25: 37 */   private final int NOT_ANALYZED = -1;
/*  26: 38 */   private final int IN_THE_QUEUE = -2;
/*  27: 39 */   private final int GRAY_LEVELS = 256;
/*  28:    */   private int xdim;
/*  29:    */   private int ydim;
/*  30:    */   
/*  31:    */   public MaxTree_yedek()
/*  32:    */   {
/*  33: 45 */     this.inputFields = "input";
/*  34: 46 */     this.outputFields = "root";
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void execute()
/*  38:    */     throws GlobalException
/*  39:    */   {
/*  40: 52 */     if (this.input.getCDim() != 1) {
/*  41: 52 */       throw new GlobalException("Only grayscale images for now");
/*  42:    */     }
/*  43: 53 */     this.xdim = this.input.getXDim();
/*  44: 54 */     this.ydim = this.input.getYDim();
/*  45:    */     
/*  46:    */ 
/*  47: 57 */     this.numberOfNodes = new int[256];
/*  48: 58 */     Arrays.fill(this.numberOfNodes, 0);
/*  49:    */     
/*  50:    */ 
/*  51: 61 */     this.nodes = new ArrayList[256];
/*  52: 62 */     for (int i = 0; i < this.nodes.length; i++) {
/*  53: 63 */       this.nodes[i] = new ArrayList();
/*  54:    */     }
/*  55: 66 */     this.nodeAtLevel = new boolean[256];
/*  56: 67 */     Arrays.fill(this.nodeAtLevel, false);
/*  57:    */     
/*  58:    */ 
/*  59: 70 */     this.statuses = new IntegerImage(this.input, false);
/*  60: 71 */     this.statuses.fill(-1);
/*  61:    */     
/*  62:    */ 
/*  63: 74 */     this.queue = new HierarchicalQueue(256);
/*  64:    */     
/*  65:    */ 
/*  66: 77 */     Point p = this.input.getXYCMinimum(0);
/*  67:    */     
/*  68:    */ 
/*  69: 80 */     this.queue.add(p, this.input.getXYByte(p.x, p.y));
/*  70:    */     
/*  71:    */ 
/*  72: 83 */     flood(this.input.getXYByte(p.x, p.y));
/*  73: 91 */     for (int x = 0; x < this.xdim; x++) {
/*  74: 92 */       for (int y = 0; y < this.ydim; y++)
/*  75:    */       {
/*  76: 93 */         int status = this.statuses.getXYInt(x, y);
/*  77: 94 */         int original = this.input.getXYByte(x, y);
/*  78:    */         
/*  79:    */ 
/*  80: 97 */         Node_yedek C = null;
/*  81: 99 */         for (Node_yedek n : this.nodes[original]) {
/*  82:100 */           if (n.status == status)
/*  83:    */           {
/*  84:101 */             C = n;
/*  85:102 */             break;
/*  86:    */           }
/*  87:    */         }
/*  88:106 */         if (C == null) {
/*  89:106 */           throw new GlobalException("The node hasn't been created?! status:" + status + " original:" + original);
/*  90:    */         }
/*  91:109 */         Point q = new Point(x, y);
/*  92:110 */         C.pixels.add(q);
/*  93:112 */         while (C.parent != null)
/*  94:    */         {
/*  95:113 */           C = C.parent;
/*  96:114 */           C.pixels.add(q);
/*  97:    */         }
/*  98:    */       }
/*  99:    */     }
/* 100:    */   }
/* 101:    */   
/* 102:    */   private int flood(int h)
/* 103:    */   {
/* 104:125 */     while (!this.queue.isEmpty(h))
/* 105:    */     {
/* 106:126 */       Point p = this.queue.get(h);
/* 107:127 */       this.statuses.setXYInt(p.x, p.y, this.numberOfNodes[h]);
/* 108:130 */       for (int i = 0; i < Tools.N.length; i++)
/* 109:    */       {
/* 110:131 */         int _x = p.x + Tools.N[i].x;
/* 111:132 */         int _y = p.y + Tools.N[i].y;
/* 112:135 */         if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim)) {
/* 113:137 */           if (this.statuses.getXYInt(_x, _y) == -1)
/* 114:    */           {
/* 115:138 */             this.queue.add(new Point(_x, _y), this.input.getXYByte(_x, _y));
/* 116:    */             
/* 117:140 */             this.statuses.setXYInt(_x, _y, -2);
/* 118:    */             
/* 119:142 */             this.nodeAtLevel[this.input.getXYByte(_x, _y)] = true;
/* 120:144 */             if (this.input.getXYByte(_x, _y) > this.input.getXYByte(p.x, p.y))
/* 121:    */             {
/* 122:146 */               int m = this.input.getXYByte(_x, _y);
/* 123:    */               do
/* 124:    */               {
/* 125:149 */                 m = flood(m);
/* 126:150 */               } while (m != h);
/* 127:    */             }
/* 128:    */           }
/* 129:    */         }
/* 130:    */       }
/* 131:    */     }
/* 132:156 */     this.numberOfNodes[h] += 1;
/* 133:    */     
/* 134:158 */     int m = h - 1;
/* 135:161 */     while ((m >= 0) && (this.nodeAtLevel[m] == 0)) {
/* 136:161 */       m--;
/* 137:    */     }
/* 138:163 */     int i = this.numberOfNodes[h] - 1;
/* 139:    */     Node_yedek CHI;
/* 140:165 */     if (m >= 0)
/* 141:    */     {
/* 142:166 */       int j = this.numberOfNodes[m];
/* 143:    */       
/* 144:    */ 
/* 145:    */ 
/* 146:    */ 
/* 147:    */ 
/* 148:    */ 
/* 149:    */ 
/* 150:174 */       Node_yedek CMJ = null;
/* 151:175 */       CHI = null;
/* 152:177 */       for (Node_yedek n : this.nodes[m]) {
/* 153:178 */         if (n.status == j)
/* 154:    */         {
/* 155:179 */           CMJ = n;
/* 156:180 */           break;
/* 157:    */         }
/* 158:    */       }
/* 159:184 */       if (CMJ == null)
/* 160:    */       {
/* 161:185 */         CMJ = new Node_yedek(j, m);
/* 162:186 */         this.nodes[m].add(CMJ);
/* 163:    */       }
/* 164:189 */       for (Node_yedek n : this.nodes[h]) {
/* 165:190 */         if (n.status == i)
/* 166:    */         {
/* 167:191 */           CHI = n;
/* 168:192 */           break;
/* 169:    */         }
/* 170:    */       }
/* 171:196 */       if (CHI == null)
/* 172:    */       {
/* 173:197 */         CHI = new Node_yedek(i, h);
/* 174:198 */         this.nodes[h].add(CHI);
/* 175:    */       }
/* 176:201 */       CHI.parent = CMJ;
/* 177:202 */       CMJ.children.add(CHI);
/* 178:    */     }
/* 179:    */     else
/* 180:    */     {
/* 181:205 */       Node_yedek CHI = null;
/* 182:207 */       for (Node_yedek n : this.nodes[h]) {
/* 183:208 */         if (n.status == i)
/* 184:    */         {
/* 185:209 */           CHI = n;
/* 186:210 */           break;
/* 187:    */         }
/* 188:    */       }
/* 189:214 */       if (CHI == null)
/* 190:    */       {
/* 191:215 */         CHI = new Node_yedek(i, h);
/* 192:216 */         this.nodes[h].add(CHI);
/* 193:    */       }
/* 194:219 */       this.root = CHI;
/* 195:    */     }
/* 196:222 */     this.nodeAtLevel[h] = false;
/* 197:    */     
/* 198:224 */     return m;
/* 199:    */   }
/* 200:    */   
/* 201:    */   public static void printTree(Node_yedek root)
/* 202:    */   {
/* 203:229 */     if (root != null)
/* 204:    */     {
/* 205:230 */       System.err.println("I am pixel " + root.level + " and of status " + root.status);
/* 206:231 */       System.err.println("I have " + root.children.size() + " children");
/* 207:232 */       System.err.println("I have " + root.pixels.size() + " pixels");
/* 208:234 */       for (Node_yedek childNode : root.children) {
/* 209:235 */         printTree(childNode);
/* 210:    */       }
/* 211:    */     }
/* 212:    */   }
/* 213:    */   
/* 214:    */   public static Image tree2Image(Node_yedek root, int xdim, int ydim)
/* 215:    */   {
/* 216:241 */     ByteImage img = new ByteImage(xdim, ydim, 1);
/* 217:242 */     img.fill(0);
/* 218:    */     
/* 219:244 */     fillImage(img, root);
/* 220:    */     
/* 221:246 */     return img;
/* 222:    */   }
/* 223:    */   
/* 224:    */   private static void fillImage(Image img, Node_yedek node)
/* 225:    */   {
/* 226:251 */     if (node != null)
/* 227:    */     {
/* 228:253 */       for (Point p : node.pixels) {
/* 229:254 */         img.setXYByte(p.x, p.y, node.level);
/* 230:    */       }
/* 231:256 */       for (Node_yedek childNode : node.children) {
/* 232:257 */         fillImage(img, childNode);
/* 233:    */       }
/* 234:    */     }
/* 235:    */   }
/* 236:    */   
/* 237:    */   public static Node_yedek invoke(Image input)
/* 238:    */   {
/* 239:    */     try
/* 240:    */     {
/* 241:263 */       return (Node_yedek)new MaxTree_yedek().preprocess(new Object[] { input });
/* 242:    */     }
/* 243:    */     catch (GlobalException e)
/* 244:    */     {
/* 245:265 */       e.printStackTrace();
/* 246:    */     }
/* 247:266 */     return null;
/* 248:    */   }
/* 249:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.maxtree.MaxTree_yedek
 * JD-Core Version:    0.7.0.1
 */