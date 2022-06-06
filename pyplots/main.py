
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

path = r"/home/arkadi/IdeaProjects/optica3/src/main/resources/"


def surface_plot(matrix, **kwargs):
    # acquire the cartesian coordinate matrices from the matrix
    # x is cols, y is rows
    (x, y) = np.meshgrid(np.arange(matrix.shape[0]), np.arange(matrix.shape[1]))
    fig = plt.figure()
    ax = fig.add_subplot(111, projection='3d')
    surf = ax.plot_surface(x, y, matrix, **kwargs)
    return (fig, ax, surf)


def build_plot_from_exel(filename):
    df = pd.read_excel(path + filename + ".xlsx")
    (fig, ax, surf) = surface_plot(df.to_numpy(), cmap=plt.cm.coolwarm)
    fig.colorbar(surf)
    ax.set_xlabel('X (cols)')
    ax.set_ylabel('Y (rows)')
    ax.set_zlabel('Z (values)')
    plt.title(filename)


if __name__ == '__main__':
    build_plot_from_exel("Restored Bessel Amplitude")
    build_plot_from_exel("Restored Bessel Phase")

    build_plot_from_exel("Restored Hankel transformed Amplitude")
    build_plot_from_exel("Restored Hankel transformed Phase")

    build_plot_from_exel("FFT transformed Amplitude")
    build_plot_from_exel("FFT transformed Phase")

    plt.show()
