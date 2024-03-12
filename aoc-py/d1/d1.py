def number_at_index(row: str, index):
    numbers = ["one", "two", "three", "four", "five", "six", "seven", "eight", "nine"]

    if row[index].isdigit():
        return int(row[index])

    for num in numbers:
        if row[index:].startswith(num):
            return numbers.index(num) + 1

    return -1


with open("d1/input.txt", encoding="utf-8") as f:

    s = 0
    for row in f:
        for i in range(len(row)):
            num = number_at_index(row, i)
            if num == -1:
                continue

            s += num * 10
            break

        for i in range(len(row) - 1, -1, -1):
            num = number_at_index(row, i)
            if num == -1:
                continue

            s += num
            break

        """
        for letter in row:
            if letter.isdigit():
                s += 10 * int(letter)
                break
        for letter in row[::-1]:
            if letter.isdigit():
                s += int(letter)
                break
        
        """
    print(s)
