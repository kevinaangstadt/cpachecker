extern int __VERIFIER_assume(int);
extern int __VERIFIER_inregex(char*, char*);

int kernel(char* input) {
    // [ab]+\\0
    __VERIFIER_assume(__VERIFIER_inregex(input, "(((0x61)|(0x62))+)(0x00)"));
    
  int offset;
  char tmp;
  char comp[4];
  int diff;
  int i;
  
  i = 0;
  offset = 0;
  
  comp[0] = 'a';
  comp[1] = 'b';
  comp[2] = 'a';
  comp[3] = '\0';
  
  diff = 0;
  
  do {
    tmp = input[offset];
    offset += 1;
    if(comp[i] != tmp)
      diff += 1;
    i += 1;
} while (tmp != '\0' && i<4);
  
  if(i<4) {
    diff += (4-i);
  }
  
  while(tmp != '\0') {
    diff += 1;
    tmp = input[offset];
    offset += 1;
  }
  
  if (diff <= 2) {
      //__VERIFIER_assume(!__VERIFIER_inregex(input, "(((0x62)((0x61)(0x61)))|((0x61)((0x62)((eps)|((0x61)|(0x62))))))((0x00)+)"));
      __VERIFIER_assume(!__VERIFIER_inregex(input, "(((0x62)((0x61)(0x61)))|(((0x61)((0x61)(0x61)))|(((0x62)((0x62)(0x61)))|((0x61)((0x62)((eps)|((0x62)|(0x61))))))))((0x00)+)"));
      goto ERROR;
  } else {
      //__VERIFIER_assume(__VERIFIER_inregex(input, "(((0x62)((0x61)(0x61)))|((0x61)((0x62)((eps)|((0x61)|(0x62))))))((0x00)+)"));
      __VERIFIER_assume(!__VERIFIER_inregex(input, "(((0x62)((0x61)(0x61)))|(((0x61)((0x61)(0x61)))|(((0x62)((0x62)(0x61)))|((0x61)((0x62)((eps)|((0x62)|(0x61))))))))((0x00)+)"));
      goto ERROR;
  }
  
  ERROR:
return diff <= 2;
}
