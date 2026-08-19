/*   1:    */ package vpt.algorithms.texture.kthtips;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.BufferedWriter;
/*   5:    */ import java.io.FileReader;
/*   6:    */ import java.io.FileWriter;
/*   7:    */ import java.io.PrintWriter;
/*   8:    */ 
/*   9:    */ public class ThreeScales
/*  10:    */ {
/*  11:    */   public static void main(String[] args)
/*  12:    */   {
/*  13: 17 */     String header = "@RELATION deneme\n@ATTRIBUTE o1\tREAL\n@ATTRIBUTE o2\tREAL\n@ATTRIBUTE o3\tREAL\n@ATTRIBUTE o4\tREAL\n@ATTRIBUTE o5\tREAL\n@ATTRIBUTE o6\tREAL\n@ATTRIBUTE o7\tREAL\n@ATTRIBUTE o8\tREAL\n@ATTRIBUTE o9\tREAL\n@ATTRIBUTE o10\tREAL\n@ATTRIBUTE o11\tREAL\n@ATTRIBUTE o12\tREAL\n@ATTRIBUTE o13\tREAL\n@ATTRIBUTE o14\tREAL\n@ATTRIBUTE o15\tREAL\n@ATTRIBUTE o16\tREAL\n@ATTRIBUTE o17\tREAL\n@ATTRIBUTE o18\tREAL\n@ATTRIBUTE o19\tREAL\n@ATTRIBUTE o20\tREAL\n@ATTRIBUTE o21\tREAL\n@ATTRIBUTE o22\tREAL\n@ATTRIBUTE o23\tREAL\n@ATTRIBUTE o24\tREAL\n@ATTRIBUTE o25\tREAL\n@ATTRIBUTE o26\tREAL\n@ATTRIBUTE o27\tREAL\n@ATTRIBUTE o28\tREAL\n@ATTRIBUTE o29\tREAL\n@ATTRIBUTE o30\tREAL\n@ATTRIBUTE o31\tREAL\n@ATTRIBUTE o32\tREAL\n@ATTRIBUTE o33\tREAL\n@ATTRIBUTE o34\tREAL\n@ATTRIBUTE o35\tREAL\n@ATTRIBUTE o36\tREAL\n@ATTRIBUTE o37\tREAL\n@ATTRIBUTE o38\tREAL\n@ATTRIBUTE o39\tREAL\n@ATTRIBUTE o40\tREAL\n@ATTRIBUTE o41\tREAL\n@ATTRIBUTE o42\tREAL\n@ATTRIBUTE o43\tREAL\n@ATTRIBUTE o44\tREAL\n@ATTRIBUTE o45\tREAL\n@ATTRIBUTE o46\tREAL\n@ATTRIBUTE o47\tREAL\n@ATTRIBUTE o48\tREAL\n@ATTRIBUTE o49\tREAL\n@ATTRIBUTE o50\tREAL\n@ATTRIBUTE o51\tREAL\n@ATTRIBUTE o52\tREAL\n@ATTRIBUTE o53\tREAL\n@ATTRIBUTE o54\tREAL\n@ATTRIBUTE o55\tREAL\n@ATTRIBUTE o56\tREAL\n@ATTRIBUTE o57\tREAL\n@ATTRIBUTE o58\tREAL\n@ATTRIBUTE o59\tREAL\n@ATTRIBUTE o60\tREAL\n@ATTRIBUTE o61\tREAL\n@ATTRIBUTE o62\tREAL\n@ATTRIBUTE o63\tREAL\n@ATTRIBUTE o64\tREAL\n@ATTRIBUTE o65\tREAL\n@ATTRIBUTE o66\tREAL\n@ATTRIBUTE o67\tREAL\n@ATTRIBUTE o68\tREAL\n@ATTRIBUTE o69\tREAL\n@ATTRIBUTE o70\tREAL\n@ATTRIBUTE o71\tREAL\n@ATTRIBUTE o72\tREAL\n@ATTRIBUTE o73\tREAL\n@ATTRIBUTE o74\tREAL\n@ATTRIBUTE o75\tREAL\n@ATTRIBUTE o76\tREAL\n@ATTRIBUTE o77\tREAL\n@ATTRIBUTE o78\tREAL\n@ATTRIBUTE o79\tREAL\n@ATTRIBUTE o80\tREAL\n@ATTRIBUTE o81\tREAL\n@ATTRIBUTE o82\tREAL\n@ATTRIBUTE o83\tREAL\n@ATTRIBUTE o84\tREAL\n@ATTRIBUTE o85\tREAL\n@ATTRIBUTE o86\tREAL\n@ATTRIBUTE o87\tREAL\n@ATTRIBUTE o88\tREAL\n@ATTRIBUTE o89\tREAL\n@ATTRIBUTE o90\tREAL\n@ATTRIBUTE o91\tREAL\n@ATTRIBUTE o92\tREAL\n@ATTRIBUTE o93\tREAL\n@ATTRIBUTE o94\tREAL\n@ATTRIBUTE o95\tREAL\n@ATTRIBUTE o96\tREAL\n@ATTRIBUTE o97\tREAL\n@ATTRIBUTE o98\tREAL\n@ATTRIBUTE o99\tREAL\n@ATTRIBUTE o100\tREAL\n@ATTRIBUTE o101\tREAL\n@ATTRIBUTE o102\tREAL\n@ATTRIBUTE o103\tREAL\n@ATTRIBUTE o104\tREAL\n@ATTRIBUTE o105\tREAL\n@ATTRIBUTE o106\tREAL\n@ATTRIBUTE o107\tREAL\n@ATTRIBUTE o108\tREAL\n@ATTRIBUTE o109\tREAL\n@ATTRIBUTE o110\tREAL\n@ATTRIBUTE o111\tREAL\n@ATTRIBUTE o112\tREAL\n@ATTRIBUTE o113\tREAL\n@ATTRIBUTE o114\tREAL\n@ATTRIBUTE o115\tREAL\n@ATTRIBUTE o116\tREAL\n@ATTRIBUTE o117\tREAL\n@ATTRIBUTE o118\tREAL\n@ATTRIBUTE o119\tREAL\n@ATTRIBUTE o120\tREAL\n@ATTRIBUTE o121\tREAL\n@ATTRIBUTE o122\tREAL\n@ATTRIBUTE o123\tREAL\n@ATTRIBUTE o124\tREAL\n@ATTRIBUTE o125\tREAL\n@ATTRIBUTE o126\tREAL\n@ATTRIBUTE o127\tREAL\n@ATTRIBUTE o128\tREAL\n@ATTRIBUTE o129\tREAL\n@ATTRIBUTE o130\tREAL\n@ATTRIBUTE o131\tREAL\n@ATTRIBUTE o132\tREAL\n@ATTRIBUTE o133\tREAL\n@ATTRIBUTE o134\tREAL\n@ATTRIBUTE o135\tREAL\n@ATTRIBUTE o136\tREAL\n@ATTRIBUTE o137\tREAL\n@ATTRIBUTE o138\tREAL\n@ATTRIBUTE o139\tREAL\n@ATTRIBUTE o140\tREAL\n@ATTRIBUTE o141\tREAL\n@ATTRIBUTE o142\tREAL\n@ATTRIBUTE o143\tREAL\n@ATTRIBUTE o144\tREAL\n@ATTRIBUTE o145\tREAL\n@ATTRIBUTE o146\tREAL\n@ATTRIBUTE o147\tREAL\n@ATTRIBUTE o148\tREAL\n@ATTRIBUTE o149\tREAL\n@ATTRIBUTE o150\tREAL\n@ATTRIBUTE o151\tREAL\n@ATTRIBUTE o152\tREAL\n@ATTRIBUTE o153\tREAL\n@ATTRIBUTE o154\tREAL\n@ATTRIBUTE o155\tREAL\n@ATTRIBUTE o156\tREAL\n@ATTRIBUTE o157\tREAL\n@ATTRIBUTE o158\tREAL\n@ATTRIBUTE o159\tREAL\n@ATTRIBUTE o160\tREAL\n@ATTRIBUTE o161\tREAL\n@ATTRIBUTE o162\tREAL\n@ATTRIBUTE o163\tREAL\n@ATTRIBUTE o164\tREAL\n@ATTRIBUTE o165\tREAL\n@ATTRIBUTE o166\tREAL\n@ATTRIBUTE o167\tREAL\n@ATTRIBUTE o168\tREAL\n@ATTRIBUTE o169\tREAL\n@ATTRIBUTE o170\tREAL\n@ATTRIBUTE o171\tREAL\n@ATTRIBUTE o172\tREAL\n@ATTRIBUTE o173\tREAL\n@ATTRIBUTE o174\tREAL\n@ATTRIBUTE o175\tREAL\n@ATTRIBUTE o176\tREAL\n@ATTRIBUTE o177\tREAL\n@ATTRIBUTE o178\tREAL\n@ATTRIBUTE o179\tREAL\n@ATTRIBUTE o180\tREAL\n@ATTRIBUTE o181\tREAL\n@ATTRIBUTE o182\tREAL\n@ATTRIBUTE o183\tREAL\n@ATTRIBUTE o184\tREAL\n@ATTRIBUTE o185\tREAL\n@ATTRIBUTE o186\tREAL\n@ATTRIBUTE o187\tREAL\n@ATTRIBUTE o188\tREAL\n@ATTRIBUTE o189\tREAL\n@ATTRIBUTE o190\tREAL\n@ATTRIBUTE o191\tREAL\n@ATTRIBUTE o192\tREAL\n@ATTRIBUTE o193\tREAL\n@ATTRIBUTE o194\tREAL\n@ATTRIBUTE o195\tREAL\n@ATTRIBUTE o196\tREAL\n@ATTRIBUTE o197\tREAL\n@ATTRIBUTE o198\tREAL\n@ATTRIBUTE o199\tREAL\n@ATTRIBUTE o200\tREAL\n@ATTRIBUTE o201\tREAL\n@ATTRIBUTE o202\tREAL\n@ATTRIBUTE o203\tREAL\n@ATTRIBUTE o204\tREAL\n@ATTRIBUTE o205\tREAL\n@ATTRIBUTE o206\tREAL\n@ATTRIBUTE o207\tREAL\n@ATTRIBUTE o208\tREAL\n@ATTRIBUTE o209\tREAL\n@ATTRIBUTE o210\tREAL\n@ATTRIBUTE o211\tREAL\n@ATTRIBUTE o212\tREAL\n@ATTRIBUTE o213\tREAL\n@ATTRIBUTE o214\tREAL\n@ATTRIBUTE o215\tREAL\n@ATTRIBUTE o216\tREAL\n@ATTRIBUTE o217\tREAL\n@ATTRIBUTE o218\tREAL\n@ATTRIBUTE o \t{0,1,2,3,4,5,6,7,8,9}\n@DATA";
/*  14:    */     try
/*  15:    */     {
/*  16:240 */       BufferedReader br = new BufferedReader(new FileReader("data.dat"));
/*  17:241 */       PrintWriter pwTr = new PrintWriter(new BufferedWriter(new FileWriter("train.arff")));
/*  18:242 */       PrintWriter pwTs = new PrintWriter(new BufferedWriter(new FileWriter("test.arff")));
/*  19:    */       
/*  20:    */ 
/*  21:245 */       pwTr.println(header);
/*  22:246 */       pwTs.println(header);
/*  23:    */       
/*  24:    */ 
/*  25:249 */       String[][] data = new String[10][81];
/*  26:251 */       for (int i = 0; i < 10; i++) {
/*  27:252 */         for (int j = 0; j < 81; j++) {
/*  28:253 */           data[i][j] = br.readLine();
/*  29:    */         }
/*  30:    */       }
/*  31:260 */       for (int i = 0; i < 10; i++)
/*  32:    */       {
/*  33:261 */         pwTr.println(data[i][0]);
/*  34:262 */         pwTr.println(data[i][4]);
/*  35:263 */         pwTr.println(data[i][8]);
/*  36:    */         
/*  37:265 */         pwTr.println(data[i][36]);
/*  38:266 */         pwTr.println(data[i][40]);
/*  39:267 */         pwTr.println(data[i][44]);
/*  40:    */         
/*  41:269 */         pwTr.println(data[i][72]);
/*  42:270 */         pwTr.println(data[i][76]);
/*  43:271 */         pwTr.println(data[i][80]);
/*  44:    */       }
/*  45:275 */       for (int i = 0; i < 10; i++) {
/*  46:276 */         for (int j = 0; j < 81; j++) {
/*  47:277 */           if (((j < 0) || (j > 8)) && 
/*  48:278 */             ((j < 36) || (j > 44)) && (
/*  49:279 */             (j < 72) || (j > 80))) {
/*  50:280 */             pwTs.println(data[i][j]);
/*  51:    */           }
/*  52:    */         }
/*  53:    */       }
/*  54:284 */       br.close();
/*  55:285 */       pwTr.close();
/*  56:286 */       pwTs.close();
/*  57:    */     }
/*  58:    */     catch (Exception e)
/*  59:    */     {
/*  60:288 */       e.printStackTrace();
/*  61:    */     }
/*  62:    */   }
/*  63:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.kthtips.ThreeScales
 * JD-Core Version:    0.7.0.1
 */