extern int __VERIFIER_assume(int);
extern int __VERIFIER_inregex(char*, char*);

int kernel(char* input) {
  // [ab]+\\0
  __VERIFIER_assume(__VERIFIER_inregex(input, "(((0x61)|(0x62))+)(0x00)"));
  
//  if (strlen(input) != 7) {
//    return 0;
//  }
  
  int offset;
  char tmp;
  char comp[7];
  int diff;
  int i;
  
  i = 0;
  offset = 0;
  
  comp[0] = 'a';
  comp[1] = 'b';
  comp[2] = 'a';
  comp[3] = 'b';
  comp[4] = 'a';
  comp[5] = 'b';
  comp[6] = '\0';
  
  diff = 0;
  
  do {
    tmp = input[offset];
    offset += 1;
    if(comp[i] != tmp)
      diff += 1;
    i += 1;
  } while (tmp != '\0' && i<7);
  
  if (diff <= 2) {
    //__VERIFIER_assume(!__VERIFIER_inregex(input, "((0x61)((0x62)((0x61)((0x62)((0x61)((eps)|((0x62)|(0x61))))))))((0x00)+)"));
    goto ERROR;
    return 1;
  } else {
    //__VERIFIER_assume(__VERIFIER_inregex(input, "((0x61)((0x62)((0x61)((0x62)((0x61)((eps)|((0x62)|(0x61))))))))((0x00)+)"));
    //goto ERROR;
    return 0;
  }
  
ERROR:
  return 0;
}
