/*   1:    */ package vpt.algorithms.mm.multi.connected.maxtree;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.LinkedList;
/*   6:    */ 
/*   7:    */ public class MyList<T>
/*   8:    */ {
/*   9:  8 */   private MyListNode<T> head = null;
/*  10:  9 */   private MyListNode<T> tail = null;
/*  11:    */   
/*  12:    */   MyList()
/*  13:    */   {
/*  14: 12 */     this.head = null;
/*  15: 13 */     this.tail = null;
/*  16:    */   }
/*  17:    */   
/*  18:    */   public static void main(String[] args)
/*  19:    */   {
/*  20: 18 */     MyList<Point> list = new MyList();
/*  21:    */     
/*  22: 20 */     list.addFirst(new Point(0, 1));
/*  23: 21 */     list.addFirst(new Point(0, 2));
/*  24: 22 */     list.addFirst(new Point(0, 3));
/*  25: 23 */     list.addFirst(new Point(0, 4));
/*  26: 24 */     list.addFirst(new Point(0, 5));
/*  27:    */     
/*  28: 26 */     LinkedList<Integer> deneme = new LinkedList();
/*  29:    */     
/*  30: 28 */     deneme.add(Integer.valueOf(1));
/*  31: 29 */     deneme.add(Integer.valueOf(2));
/*  32:    */     
/*  33: 31 */     System.err.println(deneme.removeLast());
/*  34:    */   }
/*  35:    */   
/*  36:    */   public MyListNode<T> getHead()
/*  37:    */   {
/*  38: 35 */     return this.head;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public MyListNode<T> getTail()
/*  42:    */   {
/*  43: 39 */     return this.tail;
/*  44:    */   }
/*  45:    */   
/*  46:    */   Point[] toArray()
/*  47:    */   {
/*  48: 45 */     MyListNode n = this.head;
/*  49:    */     
/*  50: 47 */     Point[] array = new Point[size()];
/*  51: 49 */     for (int i = 0; i < array.length; i++)
/*  52:    */     {
/*  53: 50 */       array[i] = ((Point)n.datum);
/*  54: 51 */       n = n.next;
/*  55:    */     }
/*  56: 54 */     return array;
/*  57:    */   }
/*  58:    */   
/*  59:    */   void addFirst(T datum)
/*  60:    */   {
/*  61: 69 */     if (datum == null) {
/*  62: 69 */       return;
/*  63:    */     }
/*  64: 71 */     MyListNode<T> ln = new MyListNode(datum);
/*  65: 73 */     if ((this.head == null) && (this.tail == null))
/*  66:    */     {
/*  67: 74 */       this.head = ln;
/*  68: 75 */       this.tail = ln;
/*  69: 76 */       ln.next = null;
/*  70: 77 */       ln.previous = null;
/*  71:    */     }
/*  72:    */     else
/*  73:    */     {
/*  74: 79 */       ln.next = this.head;
/*  75: 80 */       ln.previous = null;
/*  76: 81 */       this.head.previous = ln;
/*  77:    */       
/*  78:    */ 
/*  79: 84 */       this.head = ln;
/*  80:    */     }
/*  81:    */   }
/*  82:    */   
/*  83:    */   void addLast(T datum)
/*  84:    */   {
/*  85: 90 */     if (datum == null) {
/*  86: 90 */       return;
/*  87:    */     }
/*  88: 92 */     MyListNode<T> ln = new MyListNode(datum);
/*  89: 94 */     if ((this.head == null) && (this.tail == null))
/*  90:    */     {
/*  91: 95 */       this.head = ln;
/*  92: 96 */       this.tail = ln;
/*  93: 97 */       ln.next = null;
/*  94: 98 */       ln.previous = null;
/*  95:    */     }
/*  96:    */     else
/*  97:    */     {
/*  98:100 */       this.tail.next = ln;
/*  99:101 */       ln.previous = this.tail;
/* 100:102 */       ln.next = null;
/* 101:103 */       this.tail = ln;
/* 102:    */     }
/* 103:    */   }
/* 104:    */   
/* 105:    */   public void addAll(MyList<T> list)
/* 106:    */   {
/* 107:108 */     if (list == null) {
/* 108:108 */       return;
/* 109:    */     }
/* 110:109 */     if ((list.head == null) || (list.tail == null)) {
/* 111:109 */       return;
/* 112:    */     }
/* 113:111 */     this.tail.next = list.head;
/* 114:112 */     list.head.previous = this.tail;
/* 115:113 */     this.tail = list.tail;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public void remove(T t)
/* 119:    */   {
/* 120:120 */     MyListNode<T> n = null;
/* 121:120 */     for (MyListNode<T> tmp = this.head; tmp != null; tmp = tmp.next) {
/* 122:123 */       if (tmp.datum.equals(t))
/* 123:    */       {
/* 124:124 */         n = tmp;
/* 125:125 */         if (n.previous != null) {
/* 126:126 */           n.previous.next = n.next;
/* 127:    */         } else {
/* 128:129 */           this.head = n.next;
/* 129:    */         }
/* 130:132 */         if (n.next != null)
/* 131:    */         {
/* 132:133 */           n.next.previous = n.previous;
/* 133:134 */           break;
/* 134:    */         }
/* 135:136 */         this.tail = n.previous;
/* 136:    */         
/* 137:    */ 
/* 138:139 */         break;
/* 139:    */       }
/* 140:    */     }
/* 141:    */   }
/* 142:    */   
/* 143:    */   public int size()
/* 144:    */   {
/* 145:145 */     int size = 0;
/* 146:146 */     MyListNode<T> tmp = this.head;
/* 147:148 */     for (; tmp != null; size++) {
/* 148:149 */       tmp = tmp.next;
/* 149:    */     }
/* 150:151 */     return size;
/* 151:    */   }
/* 152:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.connected.maxtree.MyList
 * JD-Core Version:    0.7.0.1
 */