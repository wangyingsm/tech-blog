package main

import (
	"bufio"
	"fmt"
	"os"
)

func input(filename string) []string {
	var result []string
	fp, err := os.Open(filename)
	if err != nil {
		fmt.Fprintf(os.Stderr, "Can not read day7 input file, %v\n", err)
		os.Exit(-1)
	}
	reader := bufio.NewScanner(fp)
	for reader.Scan() {
		result = append(result, reader.Text())
	}
	return result
}

func question1(lines []string) map[string]int {
	result := make(map[string]int)
	for _, line := range lines {
		lc := make(map[rune]int)
		for _, r := range line {
			lc[r]++
		}
		var twice, trice bool
		for _, v := range lc {
			if v == 2 {
				twice = true
			}
			if v == 3 {
				trice = true
			}
		}
		if twice {
			result["twice"]++
		}
		if trice {
			result["trice"]++
		}
	}
	return result
}

func question2(lines []string) string {
	minDiff := 1024 * 1024 * 1024
	var common string
	for i, line := range lines {
		for _, other := range lines[i+1:] {
			diff, cstr := calDiff(line, other)
			if diff < minDiff {
				minDiff = diff
				common = cstr
			}
		}
	}
	return common
}

func calDiff(s1 string, s2 string) (int, string) {
	diffs := 0
	cstr := ""
	for i := 0; i < len(s1); i++ {
		if s1[i] == s2[i] {
			cstr += string(s1[i])
		} else {
			diffs++
		}
	}
	return diffs, cstr
}

func main() {
	lines := input("day2.txt")
	q1 := question1(lines)
	fmt.Println("Question #1:", q1["twice"]*q1["trice"])
	fmt.Println("Question #2:", question2(lines))
}
