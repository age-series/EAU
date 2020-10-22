#!/usr/bin/python3

from PIL import Image, ImageDraw

currents = [("Low", 1.0), ("Medium", 2.0), ("High", 3.0)]
insulation = [("Uninsulated", 0.0), ("300V Insulated", 0.5), ("1kV Insulated", 1.0), ("20kV Insulated", 2.0)]
materials = ["Copper", "Aluminum"]
scale = 4.0

def getName(material, current, insulation):
    return "{} Current {} {} Cable".format(current[0], material, insulation[0])


def getFileName(material, current, insulation):
    return (getName(material, current, insulation).replace(" ","") + ".png").lower()


for material in materials:
    for current in currents:
        for ins in insulation:
            print("Creating: {}".format(getName(material, current, ins)))
            image = Image.new("RGBA", (32, 32))

            draw = ImageDraw.Draw(image)

            draw.rectangle(((0,0),(31,31)), fill=(255,255,255,0))

            length = scale * (current[1] + ins[1])
            width = 1.5 * length
            con_length = scale * current[1]
            con_width = 1.5 * con_length

            draw.rectangle(
                ((15 - (length/2.0), 15 - (length/2.0)), (16 + (width/2.0), 16 + (width/2.0))),
                fill=(0, 0, 0, 255)
            )
            if material == "Aluminum":
                draw.rectangle(
                    ((15 - (con_length/2.0), 15 - (con_length/2.0)), (16 + (con_width/2.0), 16 + (con_width/2.0))),
                    fill=(208, 213, 219, 255)
                )
            else:
                draw.rectangle(
                    ((15 - (con_length/2.0), 15 - (con_length/2.0)), (16 + (con_width/2.0), 16 + (con_width/2.0))),
                    fill=(184, 115, 51, 255)
                )

            print("Writing to {}".format(getFileName(material, current, ins)))
            image.save("/home/jared/Documents/ElectricalAge/src/main/resources/assets/eln/textures/blocks/" + getFileName(material, current, ins))



