import os
import sys
FUR_COLORS = [
            "white_fur",          #/// 白色
            "orange_fur",         #// 橙色
            "magenta_fur",        #// 品红色
            "light_blue_fur",     #// 淡蓝色
            "yellow_fur",         #// 黄色
            "lime_fur",           #// 黄绿色
            "pink_fur",           #// 粉红色
            "gray_fur",
            "light_gray_fur",
            "cyan_fur",
            "purple_fur",
            "blue_fur",
            "brown_fur",
            "green_fur",
            "red_fur",
            "black_fur"
    ]
for i in FUR_COLORS:
    with open("../resources/assets/furrybohe/models/item/"+i+".json","w") as file:
        file.write("""{\"parent\": \"item/generated\",\"textures\": {\"layer0\": \"furrybohe:item/"""+i+"\"}}")
