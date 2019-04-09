/*   1:    */ package vpt.algorithms.texture.alot;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.BufferedWriter;
/*   5:    */ import java.io.FileReader;
/*   6:    */ import java.io.FileWriter;
/*   7:    */ import java.io.PrintWriter;
/*   8:    */ import java.util.Random;
/*   9:    */ 
/*  10:    */ public class RandomTrainTestFileProduction
/*  11:    */ {
/*  12:    */   public static void main(String[] args)
/*  13:    */   {
/*  14:    */     try
/*  15:    */     {
/*  16: 48 */       BufferedReader dataTr = new BufferedReader(new FileReader("alot-train.dat"));
/*  17: 49 */       BufferedReader dataTs = new BufferedReader(new FileReader("alot-test.dat"));
/*  18:    */       
/*  19: 51 */       int trainingSamples = 1;
/*  20: 52 */       int howManyTimes = 1000;
/*  21: 53 */       int sampleCount = 12;
/*  22: 54 */       int classCount = 200;
/*  23: 55 */       Random r = new Random();
/*  24:    */       
/*  25: 57 */       String header = "@RELATION deneme\n@ATTRIBUTE o1\tREAL\n@ATTRIBUTE o2\tREAL\n@ATTRIBUTE o3\tREAL\n@ATTRIBUTE o4\tREAL\n@ATTRIBUTE o5\tREAL\n@ATTRIBUTE o6\tREAL\n@ATTRIBUTE o7\tREAL\n@ATTRIBUTE o8\tREAL\n@ATTRIBUTE o9\tREAL\n@ATTRIBUTE o10\tREAL\n@ATTRIBUTE o11\tREAL\n@ATTRIBUTE o12\tREAL\n@ATTRIBUTE o13\tREAL\n@ATTRIBUTE o14\tREAL\n@ATTRIBUTE o15\tREAL\n@ATTRIBUTE o16\tREAL\n@ATTRIBUTE o17\tREAL\n@ATTRIBUTE o18\tREAL\n@ATTRIBUTE o19\tREAL\n@ATTRIBUTE o20\tREAL\n@ATTRIBUTE o21\tREAL\n@ATTRIBUTE o22\tREAL\n@ATTRIBUTE o23\tREAL\n@ATTRIBUTE o24\tREAL\n@ATTRIBUTE o25\tREAL\n@ATTRIBUTE o26\tREAL\n@ATTRIBUTE o27\tREAL\n@ATTRIBUTE o28\tREAL\n@ATTRIBUTE o29\tREAL\n@ATTRIBUTE o30\tREAL\n@ATTRIBUTE o31\tREAL\n@ATTRIBUTE o32\tREAL\n@ATTRIBUTE o33\tREAL\n@ATTRIBUTE o34\tREAL\n@ATTRIBUTE o35\tREAL\n@ATTRIBUTE o36\tREAL\n@ATTRIBUTE o37\tREAL\n@ATTRIBUTE o38\tREAL\n@ATTRIBUTE o39\tREAL\n@ATTRIBUTE o40\tREAL\n@ATTRIBUTE o41\tREAL\n@ATTRIBUTE o42\tREAL\n@ATTRIBUTE o43\tREAL\n@ATTRIBUTE o44\tREAL\n@ATTRIBUTE o45\tREAL\n@ATTRIBUTE o46\tREAL\n@ATTRIBUTE o47\tREAL\n@ATTRIBUTE o48\tREAL\n@ATTRIBUTE o49\tREAL\n@ATTRIBUTE o50\tREAL\n@ATTRIBUTE o51\tREAL\n@ATTRIBUTE o52\tREAL\n@ATTRIBUTE o53\tREAL\n@ATTRIBUTE o54\tREAL\n@ATTRIBUTE o55\tREAL\n@ATTRIBUTE o56\tREAL\n@ATTRIBUTE o57\tREAL\n@ATTRIBUTE o58\tREAL\n@ATTRIBUTE o59\tREAL\n@ATTRIBUTE o60\tREAL\n@ATTRIBUTE o61\tREAL\n@ATTRIBUTE o62\tREAL\n@ATTRIBUTE o63\tREAL\n@ATTRIBUTE o64\tREAL\n@ATTRIBUTE o65\tREAL\n@ATTRIBUTE o66\tREAL\n@ATTRIBUTE o67\tREAL\n@ATTRIBUTE o68\tREAL\n@ATTRIBUTE o69\tREAL\n@ATTRIBUTE o70\tREAL\n@ATTRIBUTE o71\tREAL\n@ATTRIBUTE o72\tREAL\n@ATTRIBUTE o73\tREAL\n@ATTRIBUTE o74\tREAL\n@ATTRIBUTE o75\tREAL\n@ATTRIBUTE o76\tREAL\n@ATTRIBUTE o77\tREAL\n@ATTRIBUTE o78\tREAL\n@ATTRIBUTE o79\tREAL\n@ATTRIBUTE o80\tREAL\n@ATTRIBUTE o81\tREAL\n@ATTRIBUTE o82\tREAL\n@ATTRIBUTE o83\tREAL\n@ATTRIBUTE o84\tREAL\n@ATTRIBUTE o85\tREAL\n@ATTRIBUTE o86\tREAL\n@ATTRIBUTE o87\tREAL\n@ATTRIBUTE o88\tREAL\n@ATTRIBUTE o89\tREAL\n@ATTRIBUTE o90\tREAL\n@ATTRIBUTE o91\tREAL\n@ATTRIBUTE o92\tREAL\n@ATTRIBUTE o93\tREAL\n@ATTRIBUTE o94\tREAL\n@ATTRIBUTE o95\tREAL\n@ATTRIBUTE o96\tREAL\n@ATTRIBUTE o97\tREAL\n@ATTRIBUTE o98\tREAL\n@ATTRIBUTE o99\tREAL\n@ATTRIBUTE o100\tREAL\n@ATTRIBUTE o101\tREAL\n@ATTRIBUTE o102\tREAL\n@ATTRIBUTE o103\tREAL\n@ATTRIBUTE o104\tREAL\n@ATTRIBUTE o105\tREAL\n@ATTRIBUTE o106\tREAL\n@ATTRIBUTE o107\tREAL\n@ATTRIBUTE o108\tREAL\n@ATTRIBUTE o109\tREAL\n@ATTRIBUTE o110\tREAL\n@ATTRIBUTE o111\tREAL\n@ATTRIBUTE o112\tREAL\n@ATTRIBUTE o113\tREAL\n@ATTRIBUTE o114\tREAL\n@ATTRIBUTE o115\tREAL\n@ATTRIBUTE o116\tREAL\n@ATTRIBUTE o117\tREAL\n@ATTRIBUTE o118\tREAL\n@ATTRIBUTE o119\tREAL\n@ATTRIBUTE o120\tREAL\n@ATTRIBUTE o121\tREAL\n@ATTRIBUTE o122\tREAL\n@ATTRIBUTE o123\tREAL\n@ATTRIBUTE o124\tREAL\n@ATTRIBUTE o125\tREAL\n@ATTRIBUTE o126\tREAL\n@ATTRIBUTE o127\tREAL\n@ATTRIBUTE o128\tREAL\n@ATTRIBUTE o129\tREAL\n@ATTRIBUTE o130\tREAL\n@ATTRIBUTE o131\tREAL\n@ATTRIBUTE o132\tREAL\n@ATTRIBUTE o133\tREAL\n@ATTRIBUTE o134\tREAL\n@ATTRIBUTE o135\tREAL\n@ATTRIBUTE o136\tREAL\n@ATTRIBUTE o137\tREAL\n@ATTRIBUTE o138\tREAL\n@ATTRIBUTE o139\tREAL\n@ATTRIBUTE o140\tREAL\n@ATTRIBUTE o141\tREAL\n@ATTRIBUTE o142\tREAL\n@ATTRIBUTE o143\tREAL\n@ATTRIBUTE o144\tREAL\n@ATTRIBUTE o145\tREAL\n@ATTRIBUTE o146\tREAL\n@ATTRIBUTE o147\tREAL\n@ATTRIBUTE o148\tREAL\n@ATTRIBUTE o149\tREAL\n@ATTRIBUTE o150\tREAL\n@ATTRIBUTE o151\tREAL\n@ATTRIBUTE o152\tREAL\n@ATTRIBUTE o153\tREAL\n@ATTRIBUTE o154\tREAL\n@ATTRIBUTE o155\tREAL\n@ATTRIBUTE o156\tREAL\n@ATTRIBUTE o157\tREAL\n@ATTRIBUTE o158\tREAL\n@ATTRIBUTE o159\tREAL\n@ATTRIBUTE o160\tREAL\n@ATTRIBUTE o161\tREAL\n@ATTRIBUTE o162\tREAL\n@ATTRIBUTE o163\tREAL\n@ATTRIBUTE o164\tREAL\n@ATTRIBUTE o165\tREAL\n@ATTRIBUTE o166\tREAL\n@ATTRIBUTE o167\tREAL\n@ATTRIBUTE o168\tREAL\n@ATTRIBUTE o169\tREAL\n@ATTRIBUTE o170\tREAL\n@ATTRIBUTE o171\tREAL\n@ATTRIBUTE o172\tREAL\n@ATTRIBUTE o173\tREAL\n@ATTRIBUTE o174\tREAL\n@ATTRIBUTE o175\tREAL\n@ATTRIBUTE o176\tREAL\n@ATTRIBUTE o177\tREAL\n@ATTRIBUTE o178\tREAL\n@ATTRIBUTE o179\tREAL\n@ATTRIBUTE o180\tREAL\n@ATTRIBUTE o181\tREAL\n@ATTRIBUTE o182\tREAL\n@ATTRIBUTE o183\tREAL\n@ATTRIBUTE o184\tREAL\n@ATTRIBUTE o185\tREAL\n@ATTRIBUTE o186\tREAL\n@ATTRIBUTE o187\tREAL\n@ATTRIBUTE o188\tREAL\n@ATTRIBUTE o189\tREAL\n@ATTRIBUTE o190\tREAL\n@ATTRIBUTE o191\tREAL\n@ATTRIBUTE o192\tREAL\n@ATTRIBUTE o193\tREAL\n@ATTRIBUTE o194\tREAL\n@ATTRIBUTE o195\tREAL\n@ATTRIBUTE o196\tREAL\n@ATTRIBUTE o197\tREAL\n@ATTRIBUTE o198\tREAL\n@ATTRIBUTE o199\tREAL\n@ATTRIBUTE o200\tREAL\n@ATTRIBUTE o201\tREAL\n@ATTRIBUTE o202\tREAL\n@ATTRIBUTE o203\tREAL\n@ATTRIBUTE o204\tREAL\n@ATTRIBUTE o205\tREAL\n@ATTRIBUTE o206\tREAL\n@ATTRIBUTE o207\tREAL\n@ATTRIBUTE o208\tREAL\n@ATTRIBUTE o209\tREAL\n@ATTRIBUTE o210\tREAL\n@ATTRIBUTE o211\tREAL\n@ATTRIBUTE o212\tREAL\n@ATTRIBUTE o213\tREAL\n@ATTRIBUTE o214\tREAL\n@ATTRIBUTE o215\tREAL\n@ATTRIBUTE o216\tREAL\n@ATTRIBUTE o217\tREAL\n@ATTRIBUTE o218\tREAL\n@ATTRIBUTE o \t{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,199}\n@DATA";
/*  26:    */       
/*  27:    */ 
/*  28:    */ 
/*  29:    */ 
/*  30:    */ 
/*  31:    */ 
/*  32:    */ 
/*  33:    */ 
/*  34:    */ 
/*  35:    */ 
/*  36:    */ 
/*  37:    */ 
/*  38:    */ 
/*  39:    */ 
/*  40:    */ 
/*  41:    */ 
/*  42:    */ 
/*  43:    */ 
/*  44:    */ 
/*  45:    */ 
/*  46:    */ 
/*  47:    */ 
/*  48:    */ 
/*  49:    */ 
/*  50:    */ 
/*  51:    */ 
/*  52:    */ 
/*  53:    */ 
/*  54:    */ 
/*  55:    */ 
/*  56:    */ 
/*  57:    */ 
/*  58:    */ 
/*  59:    */ 
/*  60:    */ 
/*  61:    */ 
/*  62:    */ 
/*  63:    */ 
/*  64:    */ 
/*  65:    */ 
/*  66:    */ 
/*  67:    */ 
/*  68:    */ 
/*  69:    */ 
/*  70:    */ 
/*  71:    */ 
/*  72:    */ 
/*  73:    */ 
/*  74:    */ 
/*  75:    */ 
/*  76:    */ 
/*  77:    */ 
/*  78:    */ 
/*  79:    */ 
/*  80:    */ 
/*  81:    */ 
/*  82:    */ 
/*  83:    */ 
/*  84:    */ 
/*  85:    */ 
/*  86:    */ 
/*  87:    */ 
/*  88:    */ 
/*  89:    */ 
/*  90:    */ 
/*  91:    */ 
/*  92:    */ 
/*  93:    */ 
/*  94:    */ 
/*  95:    */ 
/*  96:    */ 
/*  97:    */ 
/*  98:    */ 
/*  99:    */ 
/* 100:    */ 
/* 101:    */ 
/* 102:    */ 
/* 103:    */ 
/* 104:    */ 
/* 105:    */ 
/* 106:    */ 
/* 107:    */ 
/* 108:    */ 
/* 109:    */ 
/* 110:    */ 
/* 111:    */ 
/* 112:    */ 
/* 113:    */ 
/* 114:    */ 
/* 115:    */ 
/* 116:    */ 
/* 117:    */ 
/* 118:    */ 
/* 119:    */ 
/* 120:    */ 
/* 121:    */ 
/* 122:    */ 
/* 123:    */ 
/* 124:    */ 
/* 125:    */ 
/* 126:    */ 
/* 127:    */ 
/* 128:    */ 
/* 129:    */ 
/* 130:    */ 
/* 131:    */ 
/* 132:    */ 
/* 133:    */ 
/* 134:    */ 
/* 135:    */ 
/* 136:    */ 
/* 137:    */ 
/* 138:    */ 
/* 139:    */ 
/* 140:    */ 
/* 141:    */ 
/* 142:    */ 
/* 143:    */ 
/* 144:    */ 
/* 145:    */ 
/* 146:    */ 
/* 147:    */ 
/* 148:    */ 
/* 149:    */ 
/* 150:    */ 
/* 151:    */ 
/* 152:    */ 
/* 153:    */ 
/* 154:    */ 
/* 155:    */ 
/* 156:    */ 
/* 157:    */ 
/* 158:    */ 
/* 159:    */ 
/* 160:    */ 
/* 161:    */ 
/* 162:    */ 
/* 163:    */ 
/* 164:    */ 
/* 165:    */ 
/* 166:    */ 
/* 167:    */ 
/* 168:    */ 
/* 169:    */ 
/* 170:    */ 
/* 171:    */ 
/* 172:    */ 
/* 173:    */ 
/* 174:    */ 
/* 175:    */ 
/* 176:    */ 
/* 177:    */ 
/* 178:    */ 
/* 179:    */ 
/* 180:    */ 
/* 181:    */ 
/* 182:    */ 
/* 183:    */ 
/* 184:    */ 
/* 185:    */ 
/* 186:    */ 
/* 187:    */ 
/* 188:    */ 
/* 189:    */ 
/* 190:    */ 
/* 191:    */ 
/* 192:    */ 
/* 193:    */ 
/* 194:    */ 
/* 195:    */ 
/* 196:    */ 
/* 197:    */ 
/* 198:    */ 
/* 199:    */ 
/* 200:    */ 
/* 201:    */ 
/* 202:    */ 
/* 203:    */ 
/* 204:    */ 
/* 205:    */ 
/* 206:    */ 
/* 207:    */ 
/* 208:    */ 
/* 209:    */ 
/* 210:    */ 
/* 211:    */ 
/* 212:    */ 
/* 213:    */ 
/* 214:    */ 
/* 215:    */ 
/* 216:    */ 
/* 217:    */ 
/* 218:    */ 
/* 219:    */ 
/* 220:    */ 
/* 221:    */ 
/* 222:    */ 
/* 223:    */ 
/* 224:    */ 
/* 225:    */ 
/* 226:    */ 
/* 227:    */ 
/* 228:    */ 
/* 229:    */ 
/* 230:    */ 
/* 231:    */ 
/* 232:    */ 
/* 233:    */ 
/* 234:    */ 
/* 235:    */ 
/* 236:    */ 
/* 237:    */ 
/* 238:    */ 
/* 239:    */ 
/* 240:    */ 
/* 241:    */ 
/* 242:    */ 
/* 243:    */ 
/* 244:    */ 
/* 245:    */ 
/* 246:    */ 
/* 247:    */ 
/* 248:    */ 
/* 249:    */ 
/* 250:    */ 
/* 251:283 */       String[][] trainSamples = new String[classCount][sampleCount];
/* 252:284 */       String[][] testSamples = new String[classCount][sampleCount];
/* 253:287 */       for (int j = 0; j < classCount; j++) {
/* 254:288 */         for (int k = 0; k < sampleCount; k++) {
/* 255:289 */           trainSamples[j][k] = dataTr.readLine();
/* 256:    */         }
/* 257:    */       }
/* 258:294 */       for (int j = 0; j < classCount; j++) {
/* 259:295 */         for (int k = 0; k < sampleCount; k++) {
/* 260:296 */           testSamples[j][k] = dataTs.readLine();
/* 261:    */         }
/* 262:    */       }
/* 263:300 */       dataTr.close();
/* 264:301 */       dataTs.close();
/* 265:    */       
/* 266:    */ 
/* 267:304 */       PrintWriter pwTs = new PrintWriter(new BufferedWriter(new FileWriter("test.arff")));
/* 268:305 */       pwTs.println(header);
/* 269:308 */       for (int k = 0; k < classCount; k++) {
/* 270:309 */         for (int j = 0; j < sampleCount; j++) {
/* 271:310 */           pwTs.println(testSamples[k][j]);
/* 272:    */         }
/* 273:    */       }
/* 274:314 */       pwTs.close();
/* 275:    */       
/* 276:    */ 
/* 277:317 */       boolean[] flags = new boolean[sampleCount];
/* 278:319 */       for (int i = 1; i <= howManyTimes; i++)
/* 279:    */       {
/* 280:320 */         PrintWriter pwTr = new PrintWriter(new BufferedWriter(new FileWriter("train" + i + ".arff")));
/* 281:    */         
/* 282:322 */         pwTr.println(header);
/* 283:324 */         for (int j = 0; j < flags.length; j++) {
/* 284:325 */           flags[j] = false;
/* 285:    */         }
/* 286:328 */         for (int j = 0; j < trainingSamples; j++)
/* 287:    */         {
/* 288:329 */           int index = r.nextInt(sampleCount);
/* 289:331 */           while (flags[index] != 0) {
/* 290:332 */             index = r.nextInt(sampleCount);
/* 291:    */           }
/* 292:334 */           flags[index] = true;
/* 293:    */         }
/* 294:338 */         for (int k = 0; k < classCount; k++) {
/* 295:339 */           for (int j = 0; j < flags.length; j++) {
/* 296:340 */             if (flags[j] != 0) {
/* 297:341 */               pwTr.println(trainSamples[k][j]);
/* 298:    */             }
/* 299:    */           }
/* 300:    */         }
/* 301:345 */         pwTr.close();
/* 302:    */       }
/* 303:    */     }
/* 304:    */     catch (Exception e)
/* 305:    */     {
/* 306:348 */       e.printStackTrace();
/* 307:    */     }
/* 308:    */   }
/* 309:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.alot.RandomTrainTestFileProduction
 * JD-Core Version:    0.7.0.1
 */