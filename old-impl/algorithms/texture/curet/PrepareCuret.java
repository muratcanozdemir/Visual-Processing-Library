/*   1:    */ package vpt.algorithms.texture.curet;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.util.Arrays;
/*   7:    */ import vpt.Image;
/*   8:    */ import vpt.algorithms.conversion.RGB2Gray;
/*   9:    */ import vpt.algorithms.geometric.Crop;
/*  10:    */ import vpt.algorithms.io.Load;
/*  11:    */ import vpt.algorithms.io.Save;
/*  12:    */ 
/*  13:    */ public class PrepareCuret
/*  14:    */ {
/*  15:    */   public static void main(String[] args)
/*  16:    */   {
/*  17: 15 */     String path = "/home/yoktish/Downloads/data/sample";
/*  18:    */     
/*  19: 17 */     int k = 51;
/*  20: 19 */     for (int i = k; i <= k; i++)
/*  21:    */     {
/*  22: 20 */       String filename = "";
/*  23: 21 */       if (i < 10) {
/*  24: 21 */         filename = "0" + i;
/*  25:    */       } else {
/*  26: 22 */         filename = i;
/*  27:    */       }
/*  28:    */       try
/*  29:    */       {
/*  30: 25 */         File dir = new File(path + filename);
/*  31:    */         
/*  32: 27 */         String[] children = dir.list();
/*  33: 28 */         Arrays.sort(children);
/*  34: 31 */         for (int j = 0; j < children.length; j++) {
/*  35: 33 */           if (children[j].indexOf(".Z") != -1)
/*  36:    */           {
/*  37: 34 */             String command = "uncompress " + path + filename + "/" + children[j];
/*  38: 35 */             System.err.println(command + " " + children[j]);
/*  39: 36 */             Runtime.getRuntime().exec(command);
/*  40:    */           }
/*  41:    */         }
/*  42: 40 */         children = dir.list();
/*  43: 41 */         Arrays.sort(children);
/*  44: 44 */         for (int j = 0; j < children.length; j++) {
/*  45: 46 */           if ((children[j].indexOf(".txt") != -1) || (children[j].indexOf(".pht") != -1) || (children[j].indexOf(".cnr") != -1) || 
/*  46: 47 */             (children[j].indexOf(".pts") != -1) || (children[j].indexOf(".avg") != -1) || (children[j].indexOf(".cvg") != -1) || 
/*  47: 48 */             (children[j].indexOf(".meas") != -1) || (children[j].indexOf(".hist") != -1) || (children[j].indexOf(".est") != -1) || 
/*  48: 49 */             (children[j].indexOf(".feb") != -1) || (children[j].indexOf(".ovl") != -1))
/*  49:    */           {
/*  50: 50 */             String command = "rm " + path + filename + "/" + children[j];
/*  51: 51 */             Runtime.getRuntime().exec(command);
/*  52:    */           }
/*  53:    */         }
/*  54: 55 */         Thread.sleep(3000L);
/*  55:    */         
/*  56: 57 */         children = dir.list();
/*  57: 58 */         Arrays.sort(children);
/*  58:    */         
/*  59:    */ 
/*  60: 61 */         String command = "rm " + path + filename + "/" + filename + "-01-02.bmp";
/*  61: 62 */         Runtime.getRuntime().exec(command);
/*  62: 63 */         command = "rm " + path + filename + "/" + filename + "-01-05.bmp";
/*  63: 64 */         Runtime.getRuntime().exec(command);
/*  64: 65 */         command = "rm " + path + filename + "/" + filename + "-01-06.bmp";
/*  65: 66 */         Runtime.getRuntime().exec(command);
/*  66: 67 */         command = "rm " + path + filename + "/" + filename + "-01-10.bmp";
/*  67: 68 */         Runtime.getRuntime().exec(command);
/*  68: 69 */         command = "rm " + path + filename + "/" + filename + "-01-14.bmp";
/*  69: 70 */         Runtime.getRuntime().exec(command);
/*  70: 71 */         command = "rm " + path + filename + "/" + filename + "-01-15.bmp";
/*  71: 72 */         Runtime.getRuntime().exec(command);
/*  72: 73 */         command = "rm " + path + filename + "/" + filename + "-01-17.bmp";
/*  73: 74 */         Runtime.getRuntime().exec(command);
/*  74: 75 */         command = "rm " + path + filename + "/" + filename + "-01-20.bmp";
/*  75: 76 */         Runtime.getRuntime().exec(command);
/*  76: 77 */         command = "rm " + path + filename + "/" + filename + "-01-21.bmp";
/*  77: 78 */         Runtime.getRuntime().exec(command);
/*  78: 79 */         command = "rm " + path + filename + "/" + filename + "-01-23.bmp";
/*  79: 80 */         Runtime.getRuntime().exec(command);
/*  80: 81 */         command = "rm " + path + filename + "/" + filename + "-01-27.bmp";
/*  81: 82 */         Runtime.getRuntime().exec(command);
/*  82: 83 */         command = "rm " + path + filename + "/" + filename + "-01-29.bmp";
/*  83: 84 */         Runtime.getRuntime().exec(command);
/*  84: 85 */         command = "rm " + path + filename + "/" + filename + "-01-32.bmp";
/*  85: 86 */         Runtime.getRuntime().exec(command);
/*  86: 87 */         command = "rm " + path + filename + "/" + filename + "-01-33.bmp";
/*  87: 88 */         Runtime.getRuntime().exec(command);
/*  88: 89 */         command = "rm " + path + filename + "/" + filename + "-01-36.bmp";
/*  89: 90 */         Runtime.getRuntime().exec(command);
/*  90: 91 */         command = "rm " + path + filename + "/" + filename + "-01-38.bmp";
/*  91: 92 */         Runtime.getRuntime().exec(command);
/*  92: 93 */         command = "rm " + path + filename + "/" + filename + "-01-40.bmp";
/*  93: 94 */         Runtime.getRuntime().exec(command);
/*  94: 95 */         command = "rm " + path + filename + "/" + filename + "-02-02.bmp";
/*  95: 96 */         Runtime.getRuntime().exec(command);
/*  96: 97 */         command = "rm " + path + filename + "/" + filename + "-02-06.bmp";
/*  97: 98 */         Runtime.getRuntime().exec(command);
/*  98: 99 */         command = "rm " + path + filename + "/" + filename + "-02-10.bmp";
/*  99:100 */         Runtime.getRuntime().exec(command);
/* 100:101 */         command = "rm " + path + filename + "/" + filename + "-02-17.bmp";
/* 101:102 */         Runtime.getRuntime().exec(command);
/* 102:103 */         command = "rm " + path + filename + "/" + filename + "-02-20.bmp";
/* 103:104 */         Runtime.getRuntime().exec(command);
/* 104:105 */         command = "rm " + path + filename + "/" + filename + "-02-21.bmp";
/* 105:106 */         Runtime.getRuntime().exec(command);
/* 106:107 */         command = "rm " + path + filename + "/" + filename + "-02-30.bmp";
/* 107:108 */         Runtime.getRuntime().exec(command);
/* 108:109 */         command = "rm " + path + filename + "/" + filename + "-02-33.bmp";
/* 109:110 */         Runtime.getRuntime().exec(command);
/* 110:111 */         command = "rm " + path + filename + "/" + filename + "-02-36.bmp";
/* 111:112 */         Runtime.getRuntime().exec(command);
/* 112:113 */         command = "rm " + path + filename + "/" + filename + "-02-41.bmp";
/* 113:114 */         Runtime.getRuntime().exec(command);
/* 114:115 */         command = "rm " + path + filename + "/" + filename + "-02-43.bmp";
/* 115:116 */         Runtime.getRuntime().exec(command);
/* 116:117 */         command = "rm " + path + filename + "/" + filename + "-02-44.bmp";
/* 117:118 */         Runtime.getRuntime().exec(command);
/* 118:119 */         command = "rm " + path + filename + "/" + filename + "-02-47.bmp";
/* 119:120 */         Runtime.getRuntime().exec(command);
/* 120:121 */         command = "rm " + path + filename + "/" + filename + "-02-49.bmp";
/* 121:122 */         Runtime.getRuntime().exec(command);
/* 122:123 */         command = "rm " + path + filename + "/" + filename + "-02-52.bmp";
/* 123:124 */         Runtime.getRuntime().exec(command);
/* 124:125 */         command = "rm " + path + filename + "/" + filename + "-02-53.bmp";
/* 125:126 */         Runtime.getRuntime().exec(command);
/* 126:127 */         command = "rm " + path + filename + "/" + filename + "-02-56.bmp";
/* 127:128 */         Runtime.getRuntime().exec(command);
/* 128:129 */         command = "rm " + path + filename + "/" + filename + "-03-06.bmp";
/* 129:130 */         Runtime.getRuntime().exec(command);
/* 130:131 */         command = "rm " + path + filename + "/" + filename + "-03-10.bmp";
/* 131:132 */         Runtime.getRuntime().exec(command);
/* 132:133 */         command = "rm " + path + filename + "/" + filename + "-03-17.bmp";
/* 133:134 */         Runtime.getRuntime().exec(command);
/* 134:135 */         command = "rm " + path + filename + "/" + filename + "-03-21.bmp";
/* 135:136 */         Runtime.getRuntime().exec(command);
/* 136:137 */         command = "rm " + path + filename + "/" + filename + "-03-30.bmp";
/* 137:138 */         Runtime.getRuntime().exec(command);
/* 138:139 */         command = "rm " + path + filename + "/" + filename + "-03-34.bmp";
/* 139:140 */         Runtime.getRuntime().exec(command);
/* 140:141 */         command = "rm " + path + filename + "/" + filename + "-03-41.bmp";
/* 141:142 */         Runtime.getRuntime().exec(command);
/* 142:143 */         command = "rm " + path + filename + "/" + filename + "-03-44.bmp";
/* 143:144 */         Runtime.getRuntime().exec(command);
/* 144:145 */         command = "rm " + path + filename + "/" + filename + "-03-45.bmp";
/* 145:146 */         Runtime.getRuntime().exec(command);
/* 146:147 */         command = "rm " + path + filename + "/" + filename + "-03-50.bmp";
/* 147:148 */         Runtime.getRuntime().exec(command);
/* 148:149 */         command = "rm " + path + filename + "/" + filename + "-03-53.bmp";
/* 149:150 */         Runtime.getRuntime().exec(command);
/* 150:151 */         command = "rm " + path + filename + "/" + filename + "-03-57.bmp";
/* 151:152 */         Runtime.getRuntime().exec(command);
/* 152:153 */         command = "rm " + path + filename + "/" + filename + "-03-59.bmp";
/* 153:154 */         Runtime.getRuntime().exec(command);
/* 154:155 */         command = "rm " + path + filename + "/" + filename + "-03-61.bmp";
/* 155:156 */         Runtime.getRuntime().exec(command);
/* 156:157 */         command = "rm " + path + filename + "/" + filename + "-03-62.bmp";
/* 157:158 */         Runtime.getRuntime().exec(command);
/* 158:159 */         command = "rm " + path + filename + "/" + filename + "-03-64.bmp";
/* 159:160 */         Runtime.getRuntime().exec(command);
/* 160:161 */         command = "rm " + path + filename + "/" + filename + "-04-06.bmp";
/* 161:162 */         Runtime.getRuntime().exec(command);
/* 162:163 */         command = "rm " + path + filename + "/" + filename + "-04-10.bmp";
/* 163:164 */         Runtime.getRuntime().exec(command);
/* 164:165 */         command = "rm " + path + filename + "/" + filename + "-04-21.bmp";
/* 165:166 */         Runtime.getRuntime().exec(command);
/* 166:167 */         command = "rm " + path + filename + "/" + filename + "-04-24.bmp";
/* 167:168 */         Runtime.getRuntime().exec(command);
/* 168:169 */         command = "rm " + path + filename + "/" + filename + "-04-34.bmp";
/* 169:170 */         Runtime.getRuntime().exec(command);
/* 170:171 */         command = "rm " + path + filename + "/" + filename + "-04-37.bmp";
/* 171:172 */         Runtime.getRuntime().exec(command);
/* 172:173 */         command = "rm " + path + filename + "/" + filename + "-04-45.bmp";
/* 173:174 */         Runtime.getRuntime().exec(command);
/* 174:175 */         command = "rm " + path + filename + "/" + filename + "-04-48.bmp";
/* 175:176 */         Runtime.getRuntime().exec(command);
/* 176:177 */         command = "rm " + path + filename + "/" + filename + "-04-54.bmp";
/* 177:178 */         Runtime.getRuntime().exec(command);
/* 178:179 */         command = "rm " + path + filename + "/" + filename + "-04-55.bmp";
/* 179:180 */         Runtime.getRuntime().exec(command);
/* 180:181 */         command = "rm " + path + filename + "/" + filename + "-04-58.bmp";
/* 181:182 */         Runtime.getRuntime().exec(command);
/* 182:183 */         command = "rm " + path + filename + "/" + filename + "-04-60.bmp";
/* 183:184 */         Runtime.getRuntime().exec(command);
/* 184:185 */         command = "rm " + path + filename + "/" + filename + "-04-63.bmp";
/* 185:186 */         Runtime.getRuntime().exec(command);
/* 186:187 */         command = "rm " + path + filename + "/" + filename + "-05-06.bmp";
/* 187:188 */         Runtime.getRuntime().exec(command);
/* 188:189 */         command = "rm " + path + filename + "/" + filename + "-05-10.bmp";
/* 189:190 */         Runtime.getRuntime().exec(command);
/* 190:191 */         command = "rm " + path + filename + "/" + filename + "-05-14.bmp";
/* 191:192 */         Runtime.getRuntime().exec(command);
/* 192:193 */         command = "rm " + path + filename + "/" + filename + "-05-24.bmp";
/* 193:194 */         Runtime.getRuntime().exec(command);
/* 194:195 */         command = "rm " + path + filename + "/" + filename + "-05-28.bmp";
/* 195:196 */         Runtime.getRuntime().exec(command);
/* 196:197 */         command = "rm " + path + filename + "/" + filename + "-05-37.bmp";
/* 197:198 */         Runtime.getRuntime().exec(command);
/* 198:199 */         command = "rm " + path + filename + "/" + filename + "-05-39.bmp";
/* 199:200 */         Runtime.getRuntime().exec(command);
/* 200:201 */         command = "rm " + path + filename + "/" + filename + "-05-42.bmp";
/* 201:202 */         Runtime.getRuntime().exec(command);
/* 202:203 */         command = "rm " + path + filename + "/" + filename + "-05-46.bmp";
/* 203:204 */         Runtime.getRuntime().exec(command);
/* 204:205 */         command = "rm " + path + filename + "/" + filename + "-05-51.bmp";
/* 205:206 */         Runtime.getRuntime().exec(command);
/* 206:207 */         command = "rm " + path + filename + "/" + filename + "-06-10.bmp";
/* 207:208 */         Runtime.getRuntime().exec(command);
/* 208:209 */         command = "rm " + path + filename + "/" + filename + "-06-11.bmp";
/* 209:210 */         Runtime.getRuntime().exec(command);
/* 210:211 */         command = "rm " + path + filename + "/" + filename + "-06-14.bmp";
/* 211:212 */         Runtime.getRuntime().exec(command);
/* 212:213 */         command = "rm " + path + filename + "/" + filename + "-06-15.bmp";
/* 213:214 */         Runtime.getRuntime().exec(command);
/* 214:215 */         command = "rm " + path + filename + "/" + filename + "-06-18.bmp";
/* 215:216 */         Runtime.getRuntime().exec(command);
/* 216:217 */         command = "rm " + path + filename + "/" + filename + "-06-22.bmp";
/* 217:218 */         Runtime.getRuntime().exec(command);
/* 218:219 */         command = "rm " + path + filename + "/" + filename + "-06-26.bmp";
/* 219:220 */         Runtime.getRuntime().exec(command);
/* 220:221 */         command = "rm " + path + filename + "/" + filename + "-06-28.bmp";
/* 221:222 */         Runtime.getRuntime().exec(command);
/* 222:223 */         command = "rm " + path + filename + "/" + filename + "-06-31.bmp";
/* 223:224 */         Runtime.getRuntime().exec(command);
/* 224:225 */         command = "rm " + path + filename + "/" + filename + "-06-35.bmp";
/* 225:226 */         Runtime.getRuntime().exec(command);
/* 226:227 */         command = "rm " + path + filename + "/" + filename + "-07-03.bmp";
/* 227:228 */         Runtime.getRuntime().exec(command);
/* 228:229 */         command = "rm " + path + filename + "/" + filename + "-07-07.bmp";
/* 229:230 */         Runtime.getRuntime().exec(command);
/* 230:231 */         command = "rm " + path + filename + "/" + filename + "-07-11.bmp";
/* 231:232 */         Runtime.getRuntime().exec(command);
/* 232:233 */         command = "rm " + path + filename + "/" + filename + "-07-15.bmp";
/* 233:234 */         Runtime.getRuntime().exec(command);
/* 234:235 */         command = "rm " + path + filename + "/" + filename + "-07-18.bmp";
/* 235:236 */         Runtime.getRuntime().exec(command);
/* 236:237 */         command = "rm " + path + filename + "/" + filename + "-01-03.bmp";
/* 237:238 */         Runtime.getRuntime().exec(command);
/* 238:239 */         command = "rm " + path + filename + "/" + filename + "-01-07.bmp";
/* 239:240 */         Runtime.getRuntime().exec(command);
/* 240:241 */         command = "rm " + path + filename + "/" + filename + "-01-11.bmp";
/* 241:242 */         Runtime.getRuntime().exec(command);
/* 242:243 */         command = "rm " + path + filename + "/" + filename + "-01-24.bmp";
/* 243:244 */         Runtime.getRuntime().exec(command);
/* 244:245 */         command = "rm " + path + filename + "/" + filename + "-01-30.bmp";
/* 245:246 */         Runtime.getRuntime().exec(command);
/* 246:247 */         command = "rm " + path + filename + "/" + filename + "-01-34.bmp";
/* 247:248 */         Runtime.getRuntime().exec(command);
/* 248:249 */         command = "rm " + path + filename + "/" + filename + "-01-43.bmp";
/* 249:250 */         Runtime.getRuntime().exec(command);
/* 250:251 */         command = "rm " + path + filename + "/" + filename + "-01-49.bmp";
/* 251:252 */         Runtime.getRuntime().exec(command);
/* 252:253 */         command = "rm " + path + filename + "/" + filename + "-02-14.bmp";
/* 253:254 */         Runtime.getRuntime().exec(command);
/* 254:255 */         command = "rm " + path + filename + "/" + filename + "-02-24.bmp";
/* 255:256 */         Runtime.getRuntime().exec(command);
/* 256:257 */         command = "rm " + path + filename + "/" + filename + "-02-34.bmp";
/* 257:258 */         Runtime.getRuntime().exec(command);
/* 258:259 */         command = "rm " + path + filename + "/" + filename + "-02-50.bmp";
/* 259:260 */         Runtime.getRuntime().exec(command);
/* 260:261 */         command = "rm " + path + filename + "/" + filename + "-02-59.bmp";
/* 261:262 */         Runtime.getRuntime().exec(command);
/* 262:263 */         command = "rm " + path + filename + "/" + filename + "-02-61.bmp";
/* 263:264 */         Runtime.getRuntime().exec(command);
/* 264:265 */         command = "rm " + path + filename + "/" + filename + "-03-14.bmp";
/* 265:266 */         Runtime.getRuntime().exec(command);
/* 266:267 */         command = "rm " + path + filename + "/" + filename + "-03-24.bmp";
/* 267:268 */         Runtime.getRuntime().exec(command);
/* 268:269 */         command = "rm " + path + filename + "/" + filename + "-03-54.bmp";
/* 269:270 */         Runtime.getRuntime().exec(command);
/* 270:271 */         command = "rm " + path + filename + "/" + filename + "-03-60.bmp";
/* 271:272 */         Runtime.getRuntime().exec(command);
/* 272:273 */         command = "rm " + path + filename + "/" + filename + "-03-63.bmp";
/* 273:274 */         Runtime.getRuntime().exec(command);
/* 274:275 */         command = "rm " + path + filename + "/" + filename + "-04-14.bmp";
/* 275:276 */         Runtime.getRuntime().exec(command);
/* 276:277 */         command = "rm " + path + filename + "/" + filename + "-04-46.bmp";
/* 277:278 */         Runtime.getRuntime().exec(command);
/* 278:279 */         command = "rm " + path + filename + "/" + filename + "-04-51.bmp";
/* 279:280 */         Runtime.getRuntime().exec(command);
/* 280:281 */         command = "rm " + path + filename + "/" + filename + "-05-31.bmp";
/* 281:282 */         Runtime.getRuntime().exec(command);
/* 282:283 */         command = "rm " + path + filename + "/" + filename + "-05-35.bmp";
/* 283:284 */         Runtime.getRuntime().exec(command);
/* 284:285 */         command = "rm " + path + filename + "/" + filename + "-06-03.bmp";
/* 285:286 */         Runtime.getRuntime().exec(command);
/* 286:287 */         command = "rm " + path + filename + "/" + filename + "-06-07.bmp";
/* 287:288 */         Runtime.getRuntime().exec(command);
/* 288:    */         
/* 289:290 */         Thread.sleep(3000L);
/* 290:    */         
/* 291:292 */         children = dir.list();
/* 292:293 */         Arrays.sort(children);
/* 293:296 */         for (int j = 0; j < children.length; j++) {
/* 294:297 */           if (children[j].indexOf(".bmp") != -1)
/* 295:    */           {
/* 296:298 */             Image img = Load.invoke(path + filename + "/" + children[j]);
/* 297:299 */             img = Crop.invoke(img, new Point(220, 140), new Point(420, 340));
/* 298:    */             
/* 299:    */ 
/* 300:302 */             img = RGB2Gray.invoke(img);
/* 301:303 */             Save.invoke(img, path + filename + "/" + children[j]);
/* 302:    */           }
/* 303:    */         }
/* 304:    */       }
/* 305:    */       catch (Exception e)
/* 306:    */       {
/* 307:307 */         e.printStackTrace();
/* 308:    */       }
/* 309:    */     }
/* 310:    */   }
/* 311:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.curet.PrepareCuret
 * JD-Core Version:    0.7.0.1
 */