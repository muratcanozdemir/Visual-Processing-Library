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
/*  15:    */ public class MaxTree_yedek2
/*  16:    */   extends Algorithm
/*  17:    */ {
/*  18: 26 */   public Image input = null;
/*  19: 27 */   public Node_yedek root = null;
/*  20: 29 */   private Image statuses = null;
/*  21: 30 */   private int[] numberOfNodes = null;
/*  22: 31 */   private boolean[] nodeAtLevel = null;
/*  23: 32 */   private ArrayList<Node_yedek>[] nodes = null;
/*  24: 33 */   private HierarchicalQueue queue = null;
/*  25: 35 */   private final int NOT_ANALYZED = -299;
/*  26: 36 */   private final int IN_THE_QUEUE = -100;
/*  27: 37 */   private final int GRAY_LEVELS = 256;
/*  28:    */   private int xdim;
/*  29:    */   private int ydim;
/*  30:    */   
/*  31:    */   public MaxTree_yedek2()
/*  32:    */   {
/*  33: 43 */     this.inputFields = "input";
/*  34: 44 */     this.outputFields = "root";
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void execute()
/*  38:    */     throws GlobalException
/*  39:    */   {
/*  40: 50 */     if (this.input.getCDim() != 1) {
/*  41: 50 */       throw new GlobalException("Only grayscale images for now");
/*  42:    */     }
/*  43: 52 */     this.xdim = this.input.getXDim();
/*  44: 53 */     this.ydim = this.input.getYDim();
/*  45:    */     
/*  46:    */ 
/*  47: 56 */     this.numberOfNodes = new int[256];
/*  48: 57 */     Arrays.fill(this.numberOfNodes, 0);
/*  49:    */     
/*  50:    */ 
/*  51: 60 */     this.nodes = new ArrayList[256];
/*  52: 61 */     for (int i = 0; i < this.nodes.length; i++) {
/*  53: 62 */       this.nodes[i] = new ArrayList();
/*  54:    */     }
/*  55: 65 */     this.nodeAtLevel = new boolean[256];
/*  56: 66 */     Arrays.fill(this.nodeAtLevel, false);
/*  57:    */     
/*  58:    */ 
/*  59: 69 */     this.statuses = new IntegerImage(this.input, false);
/*  60: 70 */     this.statuses.fill(-299);
/*  61:    */     
/*  62:    */ 
/*  63: 73 */     this.queue = new HierarchicalQueue(256);
/*  64:    */     
/*  65:    */ 
/*  66: 76 */     Point p = this.input.getXYCMinimum(0);
/*  67:    */     
/*  68:    */ 
/*  69: 79 */     this.queue.add(p, this.input.getXYByte(p.x, p.y));
/*  70:    */     
/*  71: 81 */     System.err.println("Flooding from:" + this.input.getXYByte(p.x, p.y));
/*  72:    */     
/*  73:    */ 
/*  74: 84 */     flood(this.input.getXYByte(p.x, p.y));
/*  75: 92 */     for (int x = 0; x < this.xdim; x++) {
/*  76: 93 */       for (int y = 0; y < this.ydim; y++)
/*  77:    */       {
/*  78: 94 */         int status = this.statuses.getXYInt(x, y);
/*  79: 95 */         int original = this.input.getXYByte(x, y);
/*  80:    */         
/*  81:    */ 
/*  82: 98 */         Node_yedek C = null;
/*  83:100 */         for (Node_yedek n : this.nodes[original]) {
/*  84:101 */           if (n.status == status)
/*  85:    */           {
/*  86:102 */             C = n;
/*  87:103 */             break;
/*  88:    */           }
/*  89:    */         }
/*  90:107 */         if (C == null) {
/*  91:107 */           throw new GlobalException("The node hasn't been created?! status:" + status + " original:" + original);
/*  92:    */         }
/*  93:110 */         Point q = new Point(x, y);
/*  94:111 */         C.pixels.add(q);
/*  95:113 */         while (C.parent != null)
/*  96:    */         {
/*  97:114 */           C = C.parent;
/*  98:115 */           C.pixels.add(q);
/*  99:    */         }
/* 100:    */       }
/* 101:    */     }
/* 102:    */   }
/* 103:    */   
/* 104:    */   private int flood(int h)
/* 105:    */   {
/* 106:126 */     while (!this.queue.isEmpty(h))
/* 107:    */     {
/* 108:127 */       Point p = this.queue.get(h);
/* 109:128 */       this.statuses.setXYInt(p.x, p.y, this.numberOfNodes[h]);
/* 110:129 */       this.nodeAtLevel[h] = true;
/* 111:132 */       for (int i = 0; i < Tools.N.length; i++)
/* 112:    */       {
/* 113:133 */         int _x = p.x + Tools.N[i].x;
/* 114:134 */         int _y = p.y + Tools.N[i].y;
/* 115:137 */         if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim)) {
/* 116:139 */           if (this.statuses.getXYInt(_x, _y) == -299)
/* 117:    */           {
/* 118:140 */             this.queue.add(new Point(_x, _y), this.input.getXYByte(_x, _y));
/* 119:    */             
/* 120:142 */             this.statuses.setXYInt(_x, _y, -100);
/* 121:    */             
/* 122:144 */             this.nodeAtLevel[this.input.getXYByte(_x, _y)] = true;
/* 123:146 */             if (this.input.getXYByte(_x, _y) > this.input.getXYByte(p.x, p.y))
/* 124:    */             {
/* 125:148 */               int m = this.input.getXYByte(_x, _y);
/* 126:    */               do
/* 127:    */               {
/* 128:152 */                 m = flood(m);
/* 129:154 */               } while (m != h);
/* 130:    */             }
/* 131:    */           }
/* 132:    */         }
/* 133:    */       }
/* 134:    */     }
/* 135:160 */     this.numberOfNodes[h] += 1;
/* 136:161 */     int m = h - 1;
/* 137:163 */     while ((m >= 0) && (this.nodeAtLevel[m] == 0)) {
/* 138:163 */       m--;
/* 139:    */     }
/* 140:165 */     int i = this.numberOfNodes[h] - 1;
/* 141:    */     Node_yedek CHI;
/* 142:167 */     if (m >= 0)
/* 143:    */     {
/* 144:168 */       int j = this.numberOfNodes[m];
/* 145:    */       
/* 146:    */ 
/* 147:    */ 
/* 148:    */ 
/* 149:    */ 
/* 150:    */ 
/* 151:    */ 
/* 152:176 */       Node_yedek CMJ = null;
/* 153:177 */       CHI = null;
/* 154:179 */       for (Node_yedek n : this.nodes[m]) {
/* 155:180 */         if (n.status == j)
/* 156:    */         {
/* 157:181 */           CMJ = n;
/* 158:182 */           break;
/* 159:    */         }
/* 160:    */       }
/* 161:186 */       if (CMJ == null)
/* 162:    */       {
/* 163:187 */         CMJ = new Node_yedek(j, m);
/* 164:188 */         this.nodes[m].add(CMJ);
/* 165:    */       }
/* 166:191 */       for (Node_yedek n : this.nodes[h]) {
/* 167:192 */         if (n.status == i)
/* 168:    */         {
/* 169:193 */           CHI = n;
/* 170:194 */           break;
/* 171:    */         }
/* 172:    */       }
/* 173:198 */       if (CHI == null)
/* 174:    */       {
/* 175:199 */         CHI = new Node_yedek(i, h);
/* 176:200 */         this.nodes[h].add(CHI);
/* 177:    */       }
/* 178:203 */       CHI.parent = CMJ;
/* 179:204 */       CMJ.children.add(CHI);
/* 180:    */     }
/* 181:    */     else
/* 182:    */     {
/* 183:207 */       Node_yedek CHI = null;
/* 184:209 */       for (Node_yedek n : this.nodes[h]) {
/* 185:210 */         if (n.status == i)
/* 186:    */         {
/* 187:211 */           CHI = n;
/* 188:212 */           break;
/* 189:    */         }
/* 190:    */       }
/* 191:216 */       if (CHI == null)
/* 192:    */       {
/* 193:217 */         CHI = new Node_yedek(i, h);
/* 194:218 */         this.nodes[h].add(CHI);
/* 195:    */       }
/* 196:221 */       this.root = CHI;
/* 197:222 */       System.err.println(i + " " + h);
/* 198:    */     }
/* 199:225 */     this.nodeAtLevel[h] = false;
/* 200:    */     
/* 201:227 */     return m;
/* 202:    */   }
/* 203:    */   
/* 204:    */   public static void printTree(Node_yedek root)
/* 205:    */   {
/* 206:232 */     if (root != null)
/* 207:    */     {
/* 208:233 */       System.err.println("I am pixel " + root.level + " and of status " + root.status);
/* 209:234 */       System.err.println("I have " + root.children.size() + " children");
/* 210:235 */       System.err.println("I have " + root.pixels.size() + " pixels");
/* 211:237 */       for (Node_yedek childNode : root.children) {
/* 212:238 */         printTree(childNode);
/* 213:    */       }
/* 214:    */     }
/* 215:    */   }
/* 216:    */   
/* 217:    */   public static Image tree2Image(Node_yedek root, int xdim, int ydim)
/* 218:    */   {
/* 219:244 */     ByteImage img = new ByteImage(xdim, ydim, 1);
/* 220:245 */     img.fill(0);
/* 221:    */     
/* 222:247 */     fillImage(img, root);
/* 223:    */     
/* 224:249 */     return img;
/* 225:    */   }
/* 226:    */   
/* 227:    */   private static void fillImage(Image img, Node_yedek node)
/* 228:    */   {
/* 229:254 */     if (node != null)
/* 230:    */     {
/* 231:256 */       for (Point p : node.pixels) {
/* 232:257 */         img.setXYByte(p.x, p.y, node.level);
/* 233:    */       }
/* 234:259 */       for (Node_yedek childNode : node.children) {
/* 235:260 */         fillImage(img, childNode);
/* 236:    */       }
/* 237:    */     }
/* 238:    */   }
/* 239:    */   
/* 240:    */   public static Node_yedek invoke(Image input)
/* 241:    */   {
/* 242:    */     try
/* 243:    */     {
/* 244:266 */       return (Node_yedek)new MaxTree_yedek2().preprocess(new Object[] { input });
/* 245:    */     }
/* 246:    */     catch (GlobalException e)
/* 247:    */     {
/* 248:268 */       e.printStackTrace();
/* 249:    */     }
/* 250:269 */     return null;
/* 251:    */   }
/* 252:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.maxtree.MaxTree_yedek2
 * JD-Core Version:    0.7.0.1
 */