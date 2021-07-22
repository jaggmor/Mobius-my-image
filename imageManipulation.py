"""An environment to test complex transformations on images
2020"""

import matplotlib.pyplot as plt
import numpy as np
from scipy.ndimage import geometric_transform

#Variable declarations
img = (plt.imread('test.jpg')) # load an image
x_center = round(img.shape[1]/2)
y_center = round(img.shape[0]/2)



def normal_to_center(coords):
    return  -coords[0] + y_center,\
           coords[1] - x_center, coords[2]

def center_to_normal(coords):
   return -coords[0] + y_center,\
         x_center + coords[1], coords[2]

def tup_to_cent_complex(tup):
    x = tup[0] - x_center
    y = img.shape[0] - tup[1] - y_center
    return x + 1j*y


def mobius_transf(coords):
    coords = normal_to_center(coords)
    z = coords[1] + 1j*coords[0]
    w = (d*z-b)/(-c*z+a)
    coords = center_to_normal([w.imag, w.real, coords[2]])  #color is unchanged
    return coords 

plt.figure()
plt.imshow(img)

#Choose the points from and to!
points = plt.ginput(n=6, show_clicks=True)

zp=[tup_to_cent_complex(points[0]), tup_to_cent_complex(points[2]), tup_to_cent_complex(points[4])]; 
wa=[tup_to_cent_complex(points[1]), tup_to_cent_complex(points[3]), tup_to_cent_complex(points[5])]; 

print(zp[0], wa[0])

# transformation parameters
a = np.linalg.det([[zp[0]*wa[0], wa[0], 1], [zp[1]*wa[1], wa[1], 1], [zp[2]*wa[2], wa[2], 1]])

b = np.linalg.det([[zp[0]*wa[0], wa[0], wa[0]], [zp[1]*wa[1], wa[1], wa[1]], [zp[2]*wa[2], wa[2], wa[2]]])     

c = np.linalg.det([[zp[0], wa[0], 1], [zp[1], wa[1], 1], [zp[2], wa[2], 1]])

d = np.linalg.det([[zp[0]*wa[0], zp[0], 1],[zp[1]*wa[1], zp[1], 1],[zp[2]*wa[2], zp[2], 1]])


transformed_img = geometric_transform(img, mobius_transf, order=3)
plt.clf()
plt.imshow(transformed_img)
plt.show()

