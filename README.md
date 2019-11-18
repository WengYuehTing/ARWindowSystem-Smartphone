# Android-Gesture-Interaction

## Support 
(單/雙/三/四/五)指 單擊  
(單/雙/三/四/五)指 雙擊  
(單/雙/三/四/五)指 上滑  
(單/雙/三/四/五)指 下滑  
(單/雙/三/四/五)指 左滑  
(單/雙/三/四/五)指 右滑  
(單/雙/三/四/五)指 長按開始    
(單/雙/三/四/五)指 長按上滑  
(單/雙/三/四/五)指 長按下滑  
(單/雙/三/四/五)指 長按左滑  
(單/雙/三/四/五)指 長按下滑  
(單/雙/三/四/五)指 長按結束  
(單/雙/三/四/五)指 屏幕滑動的x和y偏移值  

## Command

Discrete rules：fingersCount + mediator(,) + gestureType.    
Continuous rules：fingersCount + mediator(,) + Move + mediator(,) + $(x_offset) + mediator(,) + $(y_offset).  
recv side：pass mediator patameter into string.split method to access actual value.  

### Discrete Command
1,OneClick  
2,OneClick  
3,OneClick  
4,OneClick  
5,OneClick  
1,DoubleClick  
2,DoubleClick  
3,DoubleClick  
4,DoubleClick  
5,DoubleClick  
1,Up  
2,Up  
3,Up  
4,Up    
5,Up  
1,Down  
2,Down  
3,Down  
4,Down  
5,Down  
1,Left  
2,Left  
3,Left  
4,Left  
5,Left   
1,Right  
2,Right   
3,Right    
4,Right    
5,Right    
1,LPStart  
2,LPStart    
3,LPStart  
4,LPStart  
5,LPStart    
1,LPEnd  
2,LPEnd  
3,LPEnd    
4,LPEnd    
5,LPEnd  
1,LPUp  
2,LPUp  
3,LPUp  
4,LPUp   
5,LPUp    
1,LPDown  
2,LPDown  
3,LPDown  
4,LPDown   
5,LPDown   
1,LPLeft  
2,LPLeft  
3,LPLeft  
4,LPLeft   
5,LPLeft  
1,LPRight  
2,LPRight  
3,LPRight  
4,LPRight   
5,LPRight 

### Continuous Command
1,Move,$(x_offset),$(y_offset)  
2,Move,$(x_offset),$(y_offset)  
3,Move,$(x_offset),$(y_offset)  
4,Move,$(x_offset),$(y_offset)  
5,Move,$(x_offset),$(y_offset)  

## Note  
GestureHandler class provide the methods mentioned above, the MULTI_FINGER_OFFSET value depends on the devices.   

## Environment  
Work on Android Studio 3.5.2, JRE 1.8.0  
