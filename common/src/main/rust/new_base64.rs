use std::collections::HashMap;
use std::sync::OnceLock;

const B64: [char; 64] = [
    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',  '/', '='
];

//base64查表
fn base64_map() -> &'static HashMap<u8, u8> {
    static HASHMAP: OnceLock<HashMap<u8, u8>> = OnceLock::new();
    HASHMAP.get_or_init(|| {
        let mut m = HashMap::new();
        for i in 0..65 {
            m.insert(B64[i] as u8,i as u8);
        }
        m
    })
}

fn encode(data: & [u8]) ->Vec<u8>{
    let lens = data.len();
    
    //剩下的字节单独处理
    let bytes_left = lens%3;
    //处理能被整除的数据
    let lens = lens - lens%3;

    let mut result = Vec::with_capacity((lens / 3) * 4 + if bytes_left > 0 { 4 } else { 0 });
    
    let index = lens/3;

    //3个字节，24位，分给4个字节，每个字节00+6位分配的
    //先处理前面3的整数倍字节
    for i in 0..index {
        let a1:u8 = data[i*3+0]>>2;
        let a2:u8 = data[i*3+0]<<6>>2 | data[i*3+1]>>4;
        let a3:u8 = data[i*3+1]<<4>>2 | data[i*3+2]>>6;
        let a4:u8 = data[i*3+2]<<2>>2;
        result.push(a1);
        result.push(a2);
        result.push(a3);
        result.push(a4);
    }

    //剩余的字节单独处理，并进行填充
    match bytes_left {
        0=>{},
        1=>{
            let a1 = data[index*3+0]>>2;
            let a2 = data[index*3+0]<<6>>2;
            result.push(a1);
            result.push(a2);
            result.push(64);
            result.push(64);

        },
        2=>{
            let a1 = data[index*3+0]>>2;
            let a2 = data[index*3+0]<<6>>2 | data[index*3+1]>>4;
            let a3 = data[index*3+1]<<4>>2;
            result.push(a1);
            result.push(a2);
            result.push(a3);
            result.push(64);
        },
        _=>{},
    }
    for i in 0..result.len() {
        result[i] = B64[result[i] as usize] as u8;
    }
    result
}
fn decode(data: &[u8])->Vec<u8> {
    
    let lens = data.len();
    let mut data = data.to_vec();
    for i in 0..lens {
        data[i] = base64_map()[&data[i]];
    }
    let mut sub_count = 0;
    let mut i = lens.saturating_sub(1); // 从末尾开始检查，确保不越界
    // 逐个检查字节
    while data[i] == 64 {
        data[i] = 0; // 设置为0
        sub_count += 1; // 计数加1
        i -= 1; // 向前移动
    }

    //向量的分配可以一开始就确定容量
    let capacity = lens*3/4;
    let mut result = Vec::with_capacity(capacity);
    let lens = lens/4;

    //按位操作，还原字节
    for index in 0..lens {
        let a1 = data[index*4+0]<<2 | data[index*4+1]>>4;
        let a2 = data[index*4+1]<<4 | data[index*4+2]>>2;
        let a3 = data[index*4+2]<<6 | data[index*4+3];
        result.push(a1 as u8);
        result.push(a2 as u8);
        result.push(a3 as u8);
    }

    //去掉填充的字符
    for _i in 0..sub_count {
        result.pop();
    }
    result
}

fn test() {
    let s = "中文abcd";
    let result = encode(s.as_bytes());

    for i in 0..result.len() {
        print!("{}",result[i] as char);
    }
    println!("");
    let s = "5Lit5paHYWJjZA==";
    let result = decode(s.as_bytes());

    match String::from_utf8(result) {
        Ok(decoded_str) => {
            println!("{}", decoded_str);
        }
        Err(e) => {
            println!("Decoding error: {}", e);
        }
    }
}